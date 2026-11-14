package com.rakshalokam.tests;

import static io.restassured.RestAssured.given;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.rakshalokam.utils.ConfigReader;

import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class RazorpayPaymentSecurityApiTest {

    private String shopApiUrl;
    private String bearerToken;

    @BeforeClass
    public void setupApiEndpoint() {
        String configuredApi = System.getProperty("shopApiUrl", ConfigReader.getProperty("shopApiUrl"));
        this.shopApiUrl = (configuredApi != null && !configuredApi.isBlank())
                ? configuredApi.trim()
                : "https://ecommerce.savista.tech/shop-api";

        String sessionCookie = System.getProperty("test.session.cookie", ConfigReader.getProperty("test.session.cookie"));
        Assert.assertNotNull(sessionCookie, "test.session.cookie is missing in config.properties.");
        this.bearerToken = extractVendureAuthToken(sessionCookie.trim());
    }

    private String extractVendureAuthToken(String rawCookie) {
        try {
            String decodedUrl = URLDecoder.decode(rawCookie, StandardCharsets.UTF_8);
            String base64Payload = decodedUrl.split("\\.")[0];
            String jsonStr = new String(Base64.getDecoder().decode(base64Payload), StandardCharsets.UTF_8);
            JsonObject json = JsonParser.parseString(jsonStr).getAsJsonObject();
            return json.get("authToken").getAsString();
        } catch (Exception e) {
            throw new RuntimeException("Failed to extract authToken from test.session.cookie", e);
        }
    }

    @Test(priority = 1, description = "Verify settled order state and total via authenticated Vendure Shop GraphQL API")
    public void testVerifySettledOrderStateViaApi() {
        String orderCode = "4HWWG8L88DLZDRBB";

        String query = """
                query GetOrderByCode($code: String!) {
                  orderByCode(code: $code) {
                    id
                    code
                    state
                    totalWithTax
                    currencyCode
                  }
                }
                """;

        Map<String, Object> variables = new HashMap<>();
        variables.put("code", orderCode);

        Map<String, Object> payload = new HashMap<>();
        payload.put("query", query);
        payload.put("variables", variables);

        Response response = given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + bearerToken)
                .body(payload)
                .when()
                .post(shopApiUrl)
                .then()
                .statusCode(200)
                .extract()
                .response();

        System.out.println("[API VERIFY] Order Query Response: " + response.asString());

        String returnedCode = response.jsonPath().getString("data.orderByCode.code");
        String state = response.jsonPath().getString("data.orderByCode.state");
        Integer totalWithTax = response.jsonPath().get("data.orderByCode.totalWithTax");

        Assert.assertEquals(returnedCode, orderCode, "Returned order code does not match queried order.");
        Assert.assertEquals(state, "PaymentSettled", "Expected order state to be PaymentSettled in Vendure backend.");
        Assert.assertNotNull(totalWithTax, "totalWithTax should not be null.");
        Assert.assertTrue(totalWithTax > 0, "Settled order totalWithTax must be greater than 0.");
    }

    @Test(priority = 2, description = "Verify backend rejects addPaymentToOrder mutation with forged Razorpay signature")
    public void testRejectForgedRazorpaySignatureViaApi() {
        String mutation = """
                mutation AddTamperedPayment($input: PaymentInput!) {
                  addPaymentToOrder(input: $input) {
                    ... on Order {
                      id
                      code
                      state
                    }
                    ... on ErrorResult {
                      errorCode
                      message
                    }
                  }
                }
                """;

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("razorpay_payment_id", "pay_FAKE9999999999");
        metadata.put("razorpay_order_id", "order_FAKE9999999999");
        metadata.put("razorpay_signature", "0000000000000000000000000000000000000000000000000000000000000000");

        Map<String, Object> input = new HashMap<>();
        input.put("method", "razorpay");
        input.put("metadata", metadata);

        Map<String, Object> variables = new HashMap<>();
        variables.put("input", input);

        Map<String, Object> payload = new HashMap<>();
        payload.put("query", mutation);
        payload.put("variables", variables);

        Response response = given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + bearerToken)
                .body(payload)
                .when()
                .post(shopApiUrl)
                .then()
                .statusCode(200)
                .extract()
                .response();

        String body = response.asString();
        System.out.println("[SECURITY API] Tampered Signature Response: " + body);

        String state = response.jsonPath().getString("data.addPaymentToOrder.state");
        Assert.assertNotEquals(state, "PaymentSettled",
                "Security vulnerability: Backend settled an order with a forged Razorpay signature!");
    }
}
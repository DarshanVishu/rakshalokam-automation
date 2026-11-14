package com.rakshalokam.tests;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class RazorpayWebhookIntegrationTest {

    // Pointing to your live backend endpoint
    private static final String BASE_URL = "https://ecommerce.savista.tech";
    private static final String WEBHOOK_PATH = "/payments/razorpay";

    // Set your webhook secret matching Vendure plugin config
    private static final String WEBHOOK_SECRET = "your_test_webhook_secret";

    @BeforeClass
    public void setup() {
        RestAssured.baseURI = BASE_URL;
    }

    private String generateSignature(String rawBody, String secret) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec keySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        mac.init(keySpec);
        byte[] hash = mac.doFinal(rawBody.getBytes(StandardCharsets.UTF_8));

        StringBuilder hex = new StringBuilder();
        for (byte b : hash) {
            String hexByte = Integer.toHexString(0xff & b);
            if (hexByte.length() == 1) hex.append('0');
            hex.append(hexByte);
        }
        return hex.toString();
    }

    @Test(priority = 1, description = "Verify webhook rejects payload with tampered or missing signature")
    public void testRejectInvalidSignature() {
        String invalidPayload = "{\"event\":\"payment.captured\",\"payload\":{}}";

        given()
                .contentType(ContentType.JSON)
                .header("User-Agent", "Mozilla/5.0")
                .header("X-Razorpay-Signature", "invalid_test_hash_000000")
                .body(invalidPayload)
                .when()
                .post(WEBHOOK_PATH)
                .then()
                .statusCode(anyOf(is(400), is(401), is(403)));
    }

    @Test(priority = 2, description = "Verify payment.captured webhook is processed and handles duplicates idempotently")
    public void testCapturedPaymentWebhookIdempotency() throws Exception {
        String paymentId = "pay_test_" + System.currentTimeMillis();
        String orderId = "order_test_" + System.currentTimeMillis();

        String payload = "{\n" +
                "  \"entity\": \"event\",\n" +
                "  \"event\": \"payment.captured\",\n" +
                "  \"payload\": {\n" +
                "    \"payment\": {\n" +
                "      \"entity\": {\n" +
                "        \"id\": \"" + paymentId + "\",\n" +
                "        \"amount\": 10000,\n" +
                "        \"currency\": \"INR\",\n" +
                "        \"status\": \"captured\",\n" +
                "        \"order_id\": \"" + orderId + "\"\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}";

        String signature = generateSignature(payload, WEBHOOK_SECRET);

        // First dispatch
        given()
                .contentType(ContentType.JSON)
                .header("User-Agent", "Mozilla/5.0")
                .header("X-Razorpay-Signature", signature)
                .body(payload)
                .when()
                .post(WEBHOOK_PATH)
                .then()
                .statusCode(anyOf(is(200), is(204)));

        // Duplicate dispatch to test idempotency
        given()
                .contentType(ContentType.JSON)
                .header("User-Agent", "Mozilla/5.0")
                .header("X-Razorpay-Signature", signature)
                .body(payload)
                .when()
                .post(WEBHOOK_PATH)
                .then()
                .statusCode(anyOf(is(200), is(204)));
    }
}
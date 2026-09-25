package com.rakshalokam.tests;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import org.testng.annotations.Test;
import com.rakshalokam.base.BaseApiTest;

public class VendureShopApiTest extends BaseApiTest {

    @Test(priority = 1, description = "Verify Remix active-order route returns 200 OK and remix header")
    public void testGetActiveOrderEndpoint() {
        given()
            .spec(storefrontApiSpec)
            .queryParam("_data", "routes/api.active-order")
        .when()
            .get("/api/active-order")
        .then()
            .statusCode(200)
            .header("x-remix-response", equalTo("yes"))
            .contentType(containsString("application/json"));
    }

    @Test(priority = 2, description = "Verify collections route loader delivers catalog category data")
    public void testGetCollectionsRouteData() {
        given()
            .spec(storefrontApiSpec)
            .queryParam("_data", "routes/collections")
        .when()
            .get("/collections")
        .then()
            .statusCode(200)
            .header("x-remix-response", equalTo("yes"))
            .contentType(containsString("application/json"));
    }

    @Test(priority = 3, description = "Verify product detail route loader returns valid product data")
    public void testGetProductDetailData() {
        given()
            .spec(storefrontApiSpec)
            .queryParam("_data", "routes/products.$slug")
        .when()
            .get("/products/one-mukhi-rudrakshi-2")
        .then()
            .statusCode(200)
            .header("x-remix-response", equalTo("yes"))
            .contentType(containsString("application/json"));
    }
}
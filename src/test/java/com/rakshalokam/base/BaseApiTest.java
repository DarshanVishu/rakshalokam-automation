package com.rakshalokam.base;

import org.testng.annotations.BeforeClass;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import com.rakshalokam.utils.ConfigReader;

public class BaseApiTest {
    protected static RequestSpecification storefrontApiSpec;

    @BeforeClass
    public void setupApiSpecification() {
        String baseUrl = ConfigReader.getProperty("baseUrl");
        if (baseUrl == null || baseUrl.isEmpty()) {
            baseUrl = "https://rakshalokam.in";
        }

        RestAssured.baseURI = baseUrl;

        storefrontApiSpec = new RequestSpecBuilder()
            .setAccept(ContentType.JSON)
            .build();
    }
}
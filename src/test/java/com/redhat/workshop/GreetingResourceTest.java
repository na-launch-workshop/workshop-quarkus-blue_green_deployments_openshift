package com.redhat.workshop;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.equalTo;

@QuarkusTest
class GreetingResourceTest {

    @Test
    void returnsDefaultGreeting() {
        given()
                .when().get("/")
                .then()
                .statusCode(200)
                .body("code", is("EN"))
                .body("message", equalTo("Hello World"));
    }
}

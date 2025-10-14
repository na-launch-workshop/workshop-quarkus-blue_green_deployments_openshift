package com.redhat.workshop;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.QuarkusTestProfile;
import io.quarkus.test.junit.TestProfile;
import java.util.Map;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@QuarkusTest
@TestProfile(GreetingResourceUnknownCountryTest.UnknownCountryProfile.class)
class GreetingResourceUnknownCountryTest {

    @Test
    void returns404ForUnknownCountry() {
        given()
                .when().get("/")
                .then()
                .statusCode(404)
                .body("error", equalTo("Unknown country code 'ZZ'"));
    }

    public static class UnknownCountryProfile implements QuarkusTestProfile {
        @Override
        public Map<String, String> getConfigOverrides() {
            return Map.of("country.code", "ZZ");
        }
    }
}

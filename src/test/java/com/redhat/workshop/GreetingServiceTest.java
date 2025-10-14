package com.redhat.workshop;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class GreetingServiceTest {

    @Inject
    GreetingService service;

    @Test
    void resolvesDefaultGreeting() {
        var greeting = service.findGreeting("en");

        assertTrue(greeting.isPresent());
        assertEquals("EN", greeting.orElseThrow().code());
        assertEquals("Hello World", greeting.orElseThrow().message());
    }

    @Test
    void returnsEmptyForUnknownCode() {
        assertTrue(service.findGreeting("zz").isEmpty());
    }

    @Test
    void normalizesWhitespaceAndCase() {
        var greeting = service.findGreeting(" fr ");

        assertTrue(greeting.isPresent());
        assertEquals("FR", greeting.orElseThrow().code());
        assertEquals("Bonjour Monde", greeting.orElseThrow().message());
    }
}

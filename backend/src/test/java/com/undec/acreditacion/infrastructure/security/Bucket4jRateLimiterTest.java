package com.undec.acreditacion.infrastructure.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class Bucket4jRateLimiterTest {

    @Test
    @DisplayName("allows requests up to capacity and rejects subsequent attempts")
    void allowsUpToCapacity() {
        Bucket4jRateLimiter limiter = new Bucket4jRateLimiter(3, 15);
        String clientKey = "192.168.1.1:admin@undec.edu.ar";

        assertTrue(limiter.tryConsume(clientKey), "1st attempt should be allowed");
        assertTrue(limiter.tryConsume(clientKey), "2nd attempt should be allowed");
        assertTrue(limiter.tryConsume(clientKey), "3rd attempt should be allowed");
        assertFalse(limiter.tryConsume(clientKey), "4th attempt must be rejected (rate limit exceeded)");
    }

    @Test
    @DisplayName("tracks rate limits separately for different client keys")
    void isolatesDifferentKeys() {
        Bucket4jRateLimiter limiter = new Bucket4jRateLimiter(2, 15);
        String clientA = "192.168.1.1:admin@undec.edu.ar";
        String clientB = "192.168.1.2:admin@undec.edu.ar";

        assertTrue(limiter.tryConsume(clientA));
        assertTrue(limiter.tryConsume(clientA));
        assertFalse(limiter.tryConsume(clientA));

        // clientB should still be allowed
        assertTrue(limiter.tryConsume(clientB), "Different client key should have its own bucket");
    }

    @Test
    @DisplayName("null or blank keys are safely allowed")
    void handlesNullOrBlank() {
        Bucket4jRateLimiter limiter = new Bucket4jRateLimiter(5, 15);
        assertTrue(limiter.tryConsume(null));
        assertTrue(limiter.tryConsume("   "));
    }
}

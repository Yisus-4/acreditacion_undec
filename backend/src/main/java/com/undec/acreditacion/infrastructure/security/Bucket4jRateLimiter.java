package com.undec.acreditacion.infrastructure.security;

import com.undec.acreditacion.application.output.LoginRateLimiter;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class Bucket4jRateLimiter implements LoginRateLimiter {

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();
    private final int capacity;
    private final int windowMinutes;

    public Bucket4jRateLimiter(
            @Value("${security.rate-limit.login.capacity:5}") int capacity,
            @Value("${security.rate-limit.login.window-minutes:15}") int windowMinutes
    ) {
        this.capacity = capacity;
        this.windowMinutes = windowMinutes;
    }

    @Override
    public boolean tryConsume(String key) {
        if (key == null || key.isBlank()) {
            return true;
        }
        Bucket bucket = buckets.computeIfAbsent(key, this::createBucket);
        return bucket.tryConsume(1);
    }

    private Bucket createBucket(String key) {
        Bandwidth limit = Bandwidth.builder()
                .capacity(capacity)
                .refillIntervally(capacity, Duration.ofMinutes(windowMinutes))
                .build();
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }
}

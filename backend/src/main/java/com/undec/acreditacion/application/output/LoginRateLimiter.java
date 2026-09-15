package com.undec.acreditacion.application.output;

public interface LoginRateLimiter {

    /**
     * Tries to consume a token for the given client key (e.g. IP + ":" + email).
     *
     * @param key client identifier
     * @return true if request is allowed, false if rate limit has been exceeded
     */
    boolean tryConsume(String key);
}

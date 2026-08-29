package com.undec.acreditacion.application.exception;

/**
 * Generic authentication failure. Deliberately carries a single constant message
 * so callers cannot distinguish between "user not found", "wrong password" and
 * "inactive user": every failed login attempt yields the same signal to avoid
 * user-enumeration leakage.
 */
public final class AuthenticationFailedException extends RuntimeException {

    public static final String GENERIC_MESSAGE = "Invalid credentials";

    public AuthenticationFailedException() {
        super(GENERIC_MESSAGE);
    }
}

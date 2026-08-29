package com.undec.acreditacion.infrastructure.auth.dto;

/**
 * Inbound credentials. Plain record: the controller performs no validation rules
 * beyond JSON deserialization; credential verification belongs to the use case.
 */
public record LoginRequest(String email, String password) {
}

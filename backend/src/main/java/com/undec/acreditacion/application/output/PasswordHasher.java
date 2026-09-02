package com.undec.acreditacion.application.output;

public interface PasswordHasher {

    String hash(String rawPassword);
    boolean matches(String rawPassword, String hashedPassword);
}

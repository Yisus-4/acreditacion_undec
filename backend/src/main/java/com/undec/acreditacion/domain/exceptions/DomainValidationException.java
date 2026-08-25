package com.undec.acreditacion.domain.exceptions;

public class DomainValidationException extends IllegalArgumentException {

    public DomainValidationException(String message) {
        super(message);
    }
}

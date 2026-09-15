package com.localy.backend.application.exception;

public class DuplicateLicenseException extends RuntimeException {

    public DuplicateLicenseException(String message) {
        super(message);
    }
}
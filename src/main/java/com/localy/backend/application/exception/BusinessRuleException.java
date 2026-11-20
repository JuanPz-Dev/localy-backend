package com.localy.backend.application.exception;

public class BusinessRuleException extends RuntimeException{
    public BusinessRuleException(String mensaje){
        super(mensaje);
    }
}

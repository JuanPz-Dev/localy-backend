package com.localy.backend.application.exception;

public class InvalidCredentialsException extends RuntimeException{
    
    public InvalidCredentialsException(String message){
        super(message);
    }
}

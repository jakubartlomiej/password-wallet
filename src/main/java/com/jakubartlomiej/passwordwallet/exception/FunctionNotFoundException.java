package com.jakubartlomiej.passwordwallet.exception;

public class FunctionNotFoundException extends IllegalArgumentException{

    public FunctionNotFoundException(String msg) {
        super(msg);
    }
}
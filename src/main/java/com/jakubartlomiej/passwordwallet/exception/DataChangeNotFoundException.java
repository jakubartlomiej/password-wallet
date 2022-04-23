package com.jakubartlomiej.passwordwallet.exception;

public class DataChangeNotFoundException extends IllegalArgumentException{

    public DataChangeNotFoundException(String msg) {
        super(msg);
    }
}
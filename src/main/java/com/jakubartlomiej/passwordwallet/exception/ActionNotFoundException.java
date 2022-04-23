package com.jakubartlomiej.passwordwallet.exception;

public class ActionNotFoundException extends IllegalArgumentException{

    public ActionNotFoundException(String msg) {
        super(msg);
    }
}
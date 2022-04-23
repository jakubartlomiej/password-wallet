package com.jakubartlomiej.passwordwallet.exception;

public class WalletNotFoundException extends IllegalArgumentException{

    public WalletNotFoundException(String msg) {
        super(msg);
    }
}
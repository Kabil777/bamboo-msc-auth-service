package com.kabi.auth_msc.exception.customExceptions;

public class CustomUserNotFoundException extends RuntimeException {
    public CustomUserNotFoundException(String msg) {
        super(msg);
    }
}

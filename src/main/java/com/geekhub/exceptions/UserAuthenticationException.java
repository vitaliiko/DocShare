package com.geekhub.exceptions;

public class UserAuthenticationException extends Exception {

    public UserAuthenticationException() {
        super();
    }

    public UserAuthenticationException(String message) {
        super(message);
    }

    public UserAuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserAuthenticationException(Throwable cause) {
        super(cause);
    }

    public UserAuthenticationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

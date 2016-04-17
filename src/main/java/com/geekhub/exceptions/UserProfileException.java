package com.geekhub.exceptions;

public class UserProfileException extends Exception {

    public UserProfileException() {
    }

    public UserProfileException(String message) {
        super(message);
    }

    public UserProfileException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserProfileException(Throwable cause) {
        super(cause);
    }

    public UserProfileException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

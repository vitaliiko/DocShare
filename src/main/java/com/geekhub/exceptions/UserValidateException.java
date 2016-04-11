package com.geekhub.exceptions;

public class UserValidateException extends Exception {

    public UserValidateException() {
        super();
    }

    public UserValidateException(String message) {
        super(message);
    }

    public UserValidateException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserValidateException(Throwable cause) {
        super(cause);
    }

    public UserValidateException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

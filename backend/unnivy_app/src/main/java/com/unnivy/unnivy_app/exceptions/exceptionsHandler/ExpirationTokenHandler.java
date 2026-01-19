package com.unnivy.unnivy_app.exceptions.exceptionsHandler;

public class ExpirationTokenHandler extends RuntimeException {

    private static final String DEFAULT_MESSAGE = "Token expirado o revocado.";

    public ExpirationTokenHandler(String message) {
        super(message);
    }

    public ExpirationTokenHandler(){
        super(DEFAULT_MESSAGE);
    }

    public ExpirationTokenHandler(String message,Throwable cause) {
        super(message,cause);
    }

    public ExpirationTokenHandler(Throwable cause) {
        super(DEFAULT_MESSAGE,cause);
    }
}

package com.unnivy.unnivy_app.exceptions.exceptionsHandler;

public class BusinessExceptionHandler extends RuntimeException {

    private static final String DEFAULT_MESSAGE = "Error de lógica de negocio";

    public BusinessExceptionHandler(String message) {
        super(message);
    }

    public BusinessExceptionHandler() {
        super(DEFAULT_MESSAGE);
    }

    public BusinessExceptionHandler(String message, Throwable cause) {
        super(message,cause);
    }



}

package com.univvy.pagos.exceptions.exceptionsHandler;

public class WebhookDeserializationException extends RuntimeException{

    private static final String DEFAULT_MESSAGE = "No se pudo deserializar.";

    public WebhookDeserializationException(String message) {
        super(message);
    }

    public WebhookDeserializationException(String message,Throwable cause) {
        super(message,cause);
    }

    public WebhookDeserializationException(Throwable cause) {
        super(DEFAULT_MESSAGE,cause);
    }

    public WebhookDeserializationException() {
        super(DEFAULT_MESSAGE);
    }

}

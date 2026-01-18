package com.univvy.pagos.exceptions.exceptionsHandler;

public class WebhookSignatureException extends RuntimeException{

    private static final String DEFAULT_MESSAGE = "Firma inválida.";

    public WebhookSignatureException(String message) {
        super(message);
    }

    public WebhookSignatureException(String message, Throwable cause) {
        super(message,cause);
    }

    public WebhookSignatureException(Throwable cause) {
        super(DEFAULT_MESSAGE,cause);
    }

    public WebhookSignatureException() {
        super(DEFAULT_MESSAGE);
    }

}

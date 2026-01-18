package com.univvy.pagos.exceptions.exceptionsHandler;

public class PagoIdNotFoundException extends RuntimeException{

    private static final String DEFAULT_MESSAGE = "No se pudo deserializar.";

    public PagoIdNotFoundException(String message) {
        super(message);
    }

    public PagoIdNotFoundException(String message,Throwable cause) {
        super(message,cause);
    }

    public PagoIdNotFoundException(Throwable cause) {
        super(DEFAULT_MESSAGE,cause);
    }

    public PagoIdNotFoundException() {
        super(DEFAULT_MESSAGE);
    }

}

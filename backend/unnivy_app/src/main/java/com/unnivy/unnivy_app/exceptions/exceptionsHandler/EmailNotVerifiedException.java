package com.unnivy.unnivy_app.exceptions.exceptionsHandler;

public class EmailNotVerifiedException extends RuntimeException {

    private static final String DEFAULT_MESSAGE = "Por favor, verifica tu email para iniciar sesión.";

    public EmailNotVerifiedException(String message) {
        super(message);
    }

    public EmailNotVerifiedException(){
      super(DEFAULT_MESSAGE);
    }

    public EmailNotVerifiedException(String message,Throwable cause) {
      super(message,cause);
    }

    public EmailNotVerifiedException(Throwable cause) {
        super(DEFAULT_MESSAGE,cause);
    }
}

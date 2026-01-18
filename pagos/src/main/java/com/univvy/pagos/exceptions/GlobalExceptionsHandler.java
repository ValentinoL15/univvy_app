package com.univvy.pagos.exceptions;

import com.univvy.pagos.dto.ErrorResponse;
import com.univvy.pagos.exceptions.exceptionsHandler.PagoIdNotFoundException;
import com.univvy.pagos.exceptions.exceptionsHandler.WebhookSignatureException;
import com.univvy.pagos.exceptions.exceptionsHandler.WebhookDeserializationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionsHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllExceptions(Exception ex, WebRequest request) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        ErrorResponse error = new ErrorResponse(
                "Ocurrió un error inesperado: " + ex.getMessage(),
                status.value(),
                status.getReasonPhrase(),
                request.getDescription(false)
        );
        return new ResponseEntity<>(error, status);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        Map<String, Object> body = new HashMap<>();
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("message", "Error de validación");
        body.put("errors", errors);

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(WebhookDeserializationException.class)
    public ResponseEntity<ErrorResponse> handleEmailNotVerifiedException(WebhookDeserializationException message,
                                                                         WebRequest request) {
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;

        ErrorResponse error = new ErrorResponse(
                message.getMessage(),
                httpStatus.value(),
                httpStatus.getReasonPhrase(),
                request.getDescription(false)
        );

        return new ResponseEntity<>(error,httpStatus);
    }

    @ExceptionHandler(PagoIdNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEmailNotVerifiedException(PagoIdNotFoundException message,
                                                                         WebRequest request) {
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;

        ErrorResponse error = new ErrorResponse(
                message.getMessage(),
                httpStatus.value(),
                httpStatus.getReasonPhrase(),
                request.getDescription(false)
        );

        return new ResponseEntity<>(error,httpStatus);
    }

    @ExceptionHandler(WebhookSignatureException.class)
    public ResponseEntity<ErrorResponse> handleEmailNotVerifiedException(WebhookSignatureException message,
                                                                         WebRequest request) {
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;

        ErrorResponse error = new ErrorResponse(
                message.getMessage(),
                httpStatus.value(),
                httpStatus.getReasonPhrase(),
                request.getDescription(false)
        );

        return new ResponseEntity<>(error,httpStatus);
    }

}

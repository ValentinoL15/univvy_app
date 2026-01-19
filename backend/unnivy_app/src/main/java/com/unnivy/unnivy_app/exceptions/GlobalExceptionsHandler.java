package com.unnivy.unnivy_app.exceptions;

import com.unnivy.unnivy_app.dto.ErrorResponse;
import com.unnivy.unnivy_app.exceptions.exceptionsHandler.AccessDeniedException;
import com.unnivy.unnivy_app.exceptions.exceptionsHandler.BusinessExceptionHandler;
import com.unnivy.unnivy_app.exceptions.exceptionsHandler.EmailNotVerifiedException;
import com.unnivy.unnivy_app.exceptions.exceptionsHandler.ExpirationTokenHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.WebRequest;

import javax.naming.AuthenticationException;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionsHandler{

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

    @ExceptionHandler(EmailNotVerifiedException.class)
    public ResponseEntity<ErrorResponse> handleEmailNotVerifiedException(EmailNotVerifiedException message,
                                                                         WebRequest request) {
        HttpStatus httpStatus = HttpStatus.UNAUTHORIZED;

        ErrorResponse error = new ErrorResponse(
            message.getMessage(),
            httpStatus.value(),
            httpStatus.getReasonPhrase(),
            request.getDescription(false)
        );

        return new ResponseEntity<>(error,httpStatus);
    }

    @ExceptionHandler(ExpirationTokenHandler.class)
    public ResponseEntity<ErrorResponse> handleTokenExpiration(ExpirationTokenHandler message,
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

    @ExceptionHandler(BusinessExceptionHandler.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessExceptionHandler message,
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

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException message,
                                                                         WebRequest request) {
        HttpStatus httpStatus = HttpStatus.FORBIDDEN;

        ErrorResponse error = new ErrorResponse(
                message.getMessage(),
                httpStatus.value(),
                httpStatus.getReasonPhrase(),
                request.getDescription(false)
        );

        return new ResponseEntity<>(error,httpStatus);
    }


}

package com.univvy.pagos.config;

import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class FeignCustomErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultDecoder = new Default();


    @Override
    public Exception decode(String methodKey, Response response) {
        try {
            String body = response.body() != null ? new String(response.body().asInputStream().readAllBytes()) : "";
            if(response.status() == 400) {
                // lanzamos la excepción con el mensaje que mandó el otro microservicio
                return new ResponseStatusException(HttpStatus.BAD_REQUEST, body);
            }
        } catch (Exception e) {
            return new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error leyendo la respuesta del microservicio");
        }
        return defaultDecoder.decode(methodKey, response);
    }

}
package com.unnivy.unnivy_app.utils;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);

        response.setContentType("application/json");

        // 3. Escribir el mensaje personalizado en el cuerpo de la respuesta
        String jsonError = "{\"status\": 403, \"error\": \"Forbidden\", \"message\": \"Usuario no permitido. Acceso denegado.\"}";

        response.getWriter().write(jsonError);
        response.getWriter().flush();
    }
}

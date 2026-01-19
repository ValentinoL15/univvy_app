package com.unnivy.unnivy_app.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.unnivy.unnivy_app.dto.UserDTOs.UserDto;
import com.unnivy.unnivy_app.model.User;
import com.unnivy.unnivy_app.repository.ITokenRepository;
import com.unnivy.unnivy_app.service.UserSeriviceImp;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final JwtUtils jwtUtils;
    private final ITokenRepository tokenRepository;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {

        final String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String jwt = authHeader.substring(7);

            try {
                DecodedJWT decodedJWT = JWT.decode(jwt);
                String username = jwtUtils.extractUsername(decodedJWT);

                // Buscamos y revocamos directamente usando el repo
                // Esto asume que tienes un método en tu repo para esto
                var validTokens = tokenRepository.findAllValidTokensByUsername(username);
                if (!validTokens.isEmpty()) {
                    validTokens.forEach(t -> {
                        t.setExpired(true);
                        t.setRevoked(true);
                    });
                    tokenRepository.saveAll(validTokens);
                }

            } catch (Exception e) {
                // Si falla la decodificación, no hacemos nada
            }
        }


        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
        response.setContentType("application/json");

        String jsonError = "{\"status\": 401, \"error\": \"Unauthorized\", \"message\": \"Falta el token o es inválido. Por favor, inicie sesión.\"}";

        response.getWriter().write(jsonError);
        response.getWriter().flush();
    }
}

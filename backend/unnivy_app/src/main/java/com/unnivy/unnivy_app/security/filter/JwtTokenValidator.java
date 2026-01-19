package com.unnivy.unnivy_app.security.filter;


import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.unnivy.unnivy_app.model.Token;
import com.unnivy.unnivy_app.model.User;
import com.unnivy.unnivy_app.repository.ITokenRepository;
import com.unnivy.unnivy_app.repository.IUserRepository;
import com.unnivy.unnivy_app.service.UserSeriviceImp;
import com.unnivy.unnivy_app.utils.CustomAuthenticationEntryPoint;
import com.unnivy.unnivy_app.utils.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JwtTokenValidator extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final ITokenRepository tokenRepository;
    private final IUserRepository userRepository;
    private final UserSeriviceImp userSeriviceImp;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        final String requestURI = request.getRequestURI();
        if (requestURI.startsWith("/api/auth") || requestURI.startsWith("/api/email")) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String jwtToken = authHeader.substring(7);

            try {
                DecodedJWT decodedJWT = jwtUtils.validateToken(jwtToken);
                String username = jwtUtils.extractUsername(decodedJWT);

                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                    // 1. Validar si el token existe y está activo en la DB
                    Token tokenEntity = tokenRepository.findByToken(jwtToken).orElse(null);
                    if (tokenEntity == null || tokenEntity.isExpired() || tokenEntity.isRevoked()) {
                        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token inválido o revocado");
                        return;
                    }

                    // 2. Obtener la entidad USER (Para cumplir con tu método isTokenValid)
                    User user = userRepository.findByUsername(username)
                            .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

                    // 3. Validar el token usando la entidad User
                    if (jwtUtils.isTokenValid(jwtToken, user)) {

                        // Necesitamos UserDetails para que Spring Security funcione correctamente
                        UserDetails userDetails = userSeriviceImp.loadUserByUsername(username);

                        // 4. CREAR EL TOKEN DE AUTENTICACIÓN
                        // Al pasar 'userDetails' como primer parámetro, el Principal dejará de ser null
                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                        // 5. ESTABLECER EL CONTEXTO UNA SOLA VEZ
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    }
                }
            } catch (Exception e) {
                SecurityContextHolder.clearContext();
                // Opcional: response.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
            }
        }

        // IMPORTANTE: Solo un doFilter al final para seguir la cadena
        filterChain.doFilter(request, response);
    }
}

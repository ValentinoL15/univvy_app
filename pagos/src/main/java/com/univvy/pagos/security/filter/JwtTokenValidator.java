package com.univvy.pagos.security.filter;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.univvy.pagos.utils.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;

@Component
@RequiredArgsConstructor
public class JwtTokenValidator extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String jwtToken = authHeader.substring(7);

            try {
                // 1. Validamos firma y expiración (Sin ir a la DB)
                DecodedJWT decodedJWT = jwtUtils.validateToken(jwtToken);

                String username = decodedJWT.getSubject();
                // 2. Extraemos los roles que guardamos previamente en el claim
                String authoritiesClaim = decodedJWT.getClaim("authorities").asString();

                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                    // 3. Convertimos los roles de String a GrantedAuthority
                    Collection<? extends GrantedAuthority> authorities =
                            AuthorityUtils.commaSeparatedStringToAuthorityList(authoritiesClaim);

                    // 4. Seteamos la autenticación en el contexto
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            username,
                            null,
                            authorities
                    );

                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            } catch (Exception e) {
                SecurityContextHolder.clearContext();
            }
        }
        filterChain.doFilter(request, response);
    }
}

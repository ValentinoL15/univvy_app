package com.univvy.pagos.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.univvy.pagos.dto.UserDTOs.UserDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Component
public class JwtUtils {

        @Value("${security.jwt.private.key}")
        private String secretKey;
        @Value("${security.jwt.user.generator}")
        private String userGenerator;

        public String extractUsername(DecodedJWT decodedJWT) {
            return decodedJWT.getSubject().toString();
        }

        public Map<String, Claim> getAllClaims(DecodedJWT decodedJWT) {
            return decodedJWT.getClaims();
        }

        public Claim getSpecificClaim(DecodedJWT decodedJWT, String claimName) {
            return decodedJWT.getClaim(claimName);
        }

        public DecodedJWT validateToken(String token) {
            try {
                Algorithm algorithm = Algorithm.HMAC256(secretKey);
                JWTVerifier jwtVerifier = JWT.require(algorithm)
                        .withIssuer(this.userGenerator)
                        .build();

                DecodedJWT decodedJWT = jwtVerifier.verify(token);
                System.out.println("Mi token: " + decodedJWT);
                return decodedJWT;
            }
            catch (JWTVerificationException exception) {
                throw new JWTVerificationException("Invalid token, not authorized");
            }
        }

        public Date extractExpiration(DecodedJWT decodedJWT) {
            return decodedJWT.getExpiresAt();
        }

        private boolean isTokenExpired(DecodedJWT decodedJWT) {
            return extractExpiration(decodedJWT).before(new Date());
        }

        public boolean isTokenValid(String token, UserDTO user) {
            try {
                DecodedJWT decodedJWT = validateToken(token);
                String username = extractUsername(decodedJWT);
                return username.equals(user.getUsername()) && !isTokenExpired(decodedJWT);
            } catch (Exception e) {
                return false;
            }
        }

        // Este método devuelve el objeto que Spring Security necesita
        public UsernamePasswordAuthenticationToken getAuthentication(String token) {
            try {
                // 1. Validamos el token (si falla, lanza excepción y va al catch)
                DecodedJWT decodedJWT = validateToken(token);

                // 2. Verificamos que no esté expirado
                if (isTokenExpired(decodedJWT)) {
                    return null;
                }

                // 3. Extraemos los datos
                String username = extractUsername(decodedJWT);
                String authority = getSpecificClaim(decodedJWT, "authorities").asString();

                // 4. Creamos el token de Spring Security
                return new UsernamePasswordAuthenticationToken(
                        username,
                        null,
                        Collections.singletonList(new SimpleGrantedAuthority(authority))
                );
            } catch (Exception e) {
                return null; // Token inválido o corrupto
            }
        }

}

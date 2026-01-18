package com.unnivy.unnivy_app.controller;

import com.unnivy.unnivy_app.dto.TokenDTOs.TokenDTO;
import com.unnivy.unnivy_app.model.Token;
import com.unnivy.unnivy_app.repository.ITokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class TokenController {

    private final ITokenRepository tokenRepository;

    @GetMapping("/api/token")
    public ResponseEntity<TokenDTO> getToken(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;
        Token token1 = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token no econtrado"));
        TokenDTO tokenDTO = new TokenDTO(token1.getUser().getUser_id(),token1.getToken(),token1.expired, token1.revoked);
        return ResponseEntity.ok(tokenDTO);
    }

}

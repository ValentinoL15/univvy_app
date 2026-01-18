package com.unnivy.unnivy_app.controller;

import com.unnivy.unnivy_app.model.ChangePassword;
import com.unnivy.unnivy_app.repository.IChangePasswordRepository;
import com.unnivy.unnivy_app.service.ForgotPassword;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/password")
@RequiredArgsConstructor
public class ForgotPasswordController {

    private final ForgotPassword forgotPasswordService;
    private final IChangePasswordRepository changePasswordRepository;

    @PostMapping("/{user_id}")
    public ResponseEntity<?> forgotPassword(@PathVariable Long user_id){
        try {
            forgotPasswordService.forgotPassword(user_id);
            return ResponseEntity.ok("Email enviado con éxito");
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/reset")
    public ResponseEntity<?> resetPassword(@RequestParam("token") String token){
        ChangePassword changePassword = changePasswordRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("El token expiró o no se encuentra disponible"));

        if (changePassword.isRevoked() || changePassword.isExpired()) {
            if (!changePassword.isRevoked()) {
                changePassword.setRevoked(true);
                changePasswordRepository.save(changePassword);
            }
            return ResponseEntity.status(HttpStatus.GONE).body("El enlace ya no es válido o ha expirado");
        }
        return ResponseEntity.ok("Token válido. Procede a cambiar la contraseña.");
    }

}

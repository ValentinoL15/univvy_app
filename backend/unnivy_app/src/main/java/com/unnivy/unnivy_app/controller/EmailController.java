package com.unnivy.unnivy_app.controller;

import com.unnivy.unnivy_app.dto.EmailDTOs.CreateVerificationCodeDTO;
import com.unnivy.unnivy_app.dto.EmailDTOs.EmailRequest;
import com.unnivy.unnivy_app.exceptions.exceptionsHandler.ExpirationTokenHandler;
import com.unnivy.unnivy_app.model.Email_Verification;
import com.unnivy.unnivy_app.model.Mail;
import com.unnivy.unnivy_app.service.EmailServiceImp;
import com.unnivy.unnivy_app.service.EmailVerificationServiceImp;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/email")
public class EmailController {

    private final EmailServiceImp emailService;
    private final EmailVerificationServiceImp emailVerificationServiceImp;

    @PostMapping("/simple")
    public void sendSimpleEmail(@RequestBody Mail mail){
        emailService.sendSimpleEmail(mail);
    }

    @PostMapping("/html")
    public void sendHTMLEmail(@RequestBody Mail mail) throws MessagingException {
        emailService.sendHTMLEmail(mail);
    }

    @PostMapping("/template")
    public void sendEmailWithThymeLeaf(@RequestBody Mail mail) throws MessagingException {
        emailService.sendEmailWithThymeLeaf(mail);
    }

    @PostMapping("/attachment")
    public void sendEmailWithAttachment(@RequestBody Mail mail) throws MessagingException {
        emailService.sendEmailWithAttachment(mail);
    }

    @PostMapping("/generate-code")
    public ResponseEntity<String> generateCode(@RequestBody EmailRequest email){
        try {
            emailVerificationServiceImp.createCodeVerification(email);
            return ResponseEntity.ok("Código reenviado con éxito");
        }catch (RuntimeException e) {
            // Manejo de la excepción si el usuario no es encontrado (lo más probable en este servicio)

            if (e.getMessage() != null && e.getMessage().contains("No se encuentra el usuario")) {
                // Devuelve 404 Not Found si el usuario no existe.
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            }

            // Manejo de cualquier otra RuntimeException con un 500 Internal Server Error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al generar el código de verificación.");
        }

    }

    @PostMapping("/verify-email/{id_verification}")
    public ResponseEntity<String> verifyEmail(@PathVariable Long id_verification,
                                              @RequestBody CreateVerificationCodeDTO code){
        try {
            String resultado = emailVerificationServiceImp.verificationEmail(id_verification,code);
            return ResponseEntity.ok(resultado);
        }
        catch (ExpirationTokenHandler e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());

        } catch (RuntimeException e) {

            if (e.getMessage().contains("No se encuentra la verificación")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno del servidor durante la verificación.");
        }
    }

}

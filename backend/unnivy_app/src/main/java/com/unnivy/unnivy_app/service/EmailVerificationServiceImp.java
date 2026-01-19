package com.unnivy.unnivy_app.service;

import com.unnivy.unnivy_app.dto.EmailDTOs.CreateVerificationCodeDTO;
import com.unnivy.unnivy_app.dto.EmailDTOs.EmailDTO;
import com.unnivy.unnivy_app.dto.EmailDTOs.EmailRequest;
import com.unnivy.unnivy_app.exceptions.exceptionsHandler.EmailNotVerifiedException;
import com.unnivy.unnivy_app.exceptions.exceptionsHandler.ExpirationTokenHandler;
import com.unnivy.unnivy_app.model.Email_Verification;
import com.unnivy.unnivy_app.model.Mail;
import com.unnivy.unnivy_app.model.User;
import com.unnivy.unnivy_app.repository.IEmailVerificationRepository;
import com.unnivy.unnivy_app.repository.IUserRepository;
import com.unnivy.unnivy_app.service.ServicesInterfaces.IEmailVerificationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class EmailVerificationServiceImp implements IEmailVerificationService {

    private final IEmailVerificationRepository emailVerificationRepository;
    private final EmailServiceImp emailServiceImp;
    private final IUserRepository userRepository;

    public static String generarCodigoCincoDigitos() {
        // Genera un número aleatorio entre 10000 (inclusive) y 99999 (inclusive).
        int min = 10000;
        int max = 99999;

        // Usamos ThreadLocalRandom, que es más eficiente en entornos multihilo.
        int codigo = ThreadLocalRandom.current().nextInt(min, max + 1);

        // Se convierte el entero a String para usarlo como código.
        return String.valueOf(codigo);
    }

    @Override
    public EmailDTO getVerification(Long id_verification) {
        Email_Verification email = emailVerificationRepository.findById(id_verification)
                .orElseThrow(() -> new RuntimeException("El email no se encuentra"));
        EmailDTO emailDTO = new EmailDTO(
                email.getUser(),
                email.getCode(),
                email.getExpiryDate(),
                email.isExpired(),
                email.isRevoked()
        );
        return emailDTO;
    }

    @Override
    @Transactional
    public void createCodeVerification(EmailRequest email) {
        User user = userRepository.findByEmail(email.email())
                .orElseThrow(() -> new RuntimeException("El email no existe, intente con otro"));

        emailVerificationRepository.revokeAllPreviousCodesByUser(user);

        Email_Verification mail = new Email_Verification();
        String codigo = generarCodigoCincoDigitos();
        mail.setCode(codigo);
        mail.setUser(user);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiryDate = now.plusMinutes(5);
        mail.setExpiryDate(expiryDate);

        emailVerificationRepository.save(mail);
        Mail mymail = new Mail(email.email(),"Verificación de Email", "");
        emailServiceImp.sendEmailWithThymeLeaf(mymail);

    }

    @Override
    public String verificationEmail(Long id_verification, CreateVerificationCodeDTO codeDTO) {

        Email_Verification emailVerification = emailVerificationRepository.findById(id_verification)
                .orElseThrow(() -> new RuntimeException("No se encuentra la verificación"));

        User user = emailVerification.getUser();

        if(emailVerification.isExpired() || emailVerification.isRevoked()) {

            if (emailVerification.isExpired() && !emailVerification.isRevoked()) {
                emailVerification.setRevoked(true);
                emailVerificationRepository.save(emailVerification);
            }

            throw new ExpirationTokenHandler("El código de verificación es inválido o ha expirado.");
        }

        System.out.println(emailVerification.getCode());
        System.out.println("real" + codeDTO);

        if(!codeDTO.code().equals(emailVerification.getCode())) {
            throw new RuntimeException("Código incorrecto");
        }

        user.setEmail_verification(true);
        userRepository.save(user);

        emailVerification.setRevoked(true);
        emailVerificationRepository.save(emailVerification);

        return "Usuario verificado con éxito";

    }
}

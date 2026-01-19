package com.unnivy.unnivy_app.service;

import com.unnivy.unnivy_app.model.Email_Verification;
import com.unnivy.unnivy_app.model.Mail;
import com.unnivy.unnivy_app.model.User;
import com.unnivy.unnivy_app.repository.IEmailVerificationRepository;
import com.unnivy.unnivy_app.repository.IUserRepository;
import com.unnivy.unnivy_app.service.ServicesInterfaces.IEmailService;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.springframework.data.domain.Pageable;

import java.util.List;


@Service
@RequiredArgsConstructor
public class EmailServiceImp implements IEmailService {

    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;
    private final IUserRepository userRepository;
    private final IEmailVerificationRepository emailVerificationRepository;

    @Override
    @Async
    public void sendSimpleEmail(Mail mail) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(mail.getTo());
        message.setSubject(mail.getSubject());
        message.setText(mail.getBody());

        javaMailSender.send(message);
    }

    @SneakyThrows
    @Override
    @Async
    public void sendHTMLEmail(Mail mail) {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom("pichilongo1@gmail.com");
        helper.setTo(mail.getTo());
        helper.setSubject(mail.getSubject());
        helper.setText(mail.getBody(), true);

        javaMailSender.send(message);
    }

    @SneakyThrows
    @Override
    public void sendEmailWithThymeLeaf(Mail mail) {
        String recipient = mail.getTo();

        User user = userRepository.findByEmail(recipient)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con el email: " + recipient));
        List<Email_Verification> activeEmails = emailVerificationRepository.findActiveVerificationByUser(
                user,
                Pageable.ofSize(1) // Se elimina el casteo incorrecto
        );

        if (activeEmails.isEmpty()) {
            throw new RuntimeException("No hay un código de verificación activo para enviar.");
        }

        String codigo = activeEmails.get(0).getCode();

        Context context = new Context();
        context.setVariable("username", user.getUsername());
        context.setVariable("codigo", codigo);

        String process = templateEngine.process("ThymeLeafMail", context);

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setSubject(mail.getSubject());
        helper.setFrom("Pichilongo1@gmail.com");
        helper.setText(process, true); // ✅ true = HTML
        helper.setTo(recipient);

        javaMailSender.send(message);
    }

    @SneakyThrows
    @Override
    public void sendEmailToChangePassword(Mail mail, String token) {
        String recipient = mail.getTo();

        User user = userRepository.findByEmail(recipient)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con el email: " + recipient));
        List<Email_Verification> activeEmails = emailVerificationRepository.findActiveVerificationByUser(
                user,
                Pageable.ofSize(1) // Se elimina el casteo incorrecto
        );

        Context context = new Context();
        context.setVariable("username", user.getUsername());
        String recoveryUrl = "https://tu-app.com/api/password/reset?token=" + token;
        context.setVariable("url", recoveryUrl);

        String process = templateEngine.process("ForgotEmail", context);

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setSubject(mail.getSubject());
        helper.setFrom("Pichilongo1@gmail.com");
        helper.setText(process, true); // ✅ true = HTML
        helper.setTo(recipient);

        javaMailSender.send(message);
    }

    @Override
    public void sendEmailWithAttachment(Mail mail) {

    }
}

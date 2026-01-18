package com.unnivy.unnivy_app.service;

import com.unnivy.unnivy_app.model.ChangePassword;
import com.unnivy.unnivy_app.model.Mail;
import com.unnivy.unnivy_app.model.User;
import com.unnivy.unnivy_app.repository.IChangePasswordRepository;
import com.unnivy.unnivy_app.repository.IUserRepository;
import com.unnivy.unnivy_app.service.ServicesInterfaces.IEmailService;
import com.unnivy.unnivy_app.service.ServicesInterfaces.IForgotPassword;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ForgotPassword implements IForgotPassword {

    private final IEmailService emailService;
    private final IUserRepository userRepository;
    private final IChangePasswordRepository changePasswordRepository;

    @Transactional
    @Override
    public void forgotPassword(Long user_id) {
        User user = userRepository.findByUserCustomId(user_id)
                .orElseThrow(() -> new RuntimeException("Usuario desconocido"));
        String token = UUID.randomUUID().toString();
        ChangePassword pass = new ChangePassword();
        pass.setEmail(user.getEmail());
        pass.setToken(token);
        pass.setExpiryDate(LocalDateTime.now().plusMinutes(15));
        changePasswordRepository.save(pass);

        Mail mymail = new Mail(user.getEmail(),"Recuperación de contraseña", "");
        emailService.sendEmailToChangePassword(mymail,token);

    }

    @Override
    public void changePassword(Long id, String password) {
        User user = userRepository.findByUserCustomId(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        user.setPassword(encryptPassword(password));
        userRepository.save(user);
    }

    @Override
    public String encryptPassword(String password) {
        return new BCryptPasswordEncoder().encode(password);
    }
}

package com.unnivy.unnivy_app.service.ServicesInterfaces;

import com.unnivy.unnivy_app.dto.EmailDTOs.CreateVerificationCodeDTO;
import com.unnivy.unnivy_app.dto.EmailDTOs.EmailDTO;
import com.unnivy.unnivy_app.dto.EmailDTOs.EmailRequest;
import com.unnivy.unnivy_app.model.User;

public interface IEmailVerificationService {

    public EmailDTO getVerification(Long id_verification);

    public void createCodeVerification(EmailRequest email);

    public String verificationEmail(Long id_verification, CreateVerificationCodeDTO code);

}

package com.unnivy.unnivy_app.service.ServicesInterfaces;

import com.unnivy.unnivy_app.model.Mail;

public interface IEmailService {

    void sendSimpleEmail(Mail mail);

    void sendHTMLEmail(Mail mail);

    void sendEmailWithThymeLeaf(Mail mail);

    void sendEmailToChangePassword(Mail mail, String token);

    void sendEmailWithAttachment(Mail mail);

}

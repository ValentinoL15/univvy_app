package com.unnivy.unnivy_app.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@Table(name = "clients")
public class Client extends User{

    public Client() {
    }

    public Client(Long user_id,String name, String lastname, String email, String password, String username, String phone, String university,RoleType role, String profile_photo,
                  LocalDate birth, boolean email_verification, boolean enabled, boolean accountNotExpired, boolean accountNotLocked, boolean credentialNotExpired) {
        super(user_id,name, lastname, email, password, username, phone, university,role, profile_photo, birth, email_verification, enabled, accountNotExpired, accountNotLocked, credentialNotExpired);
    }
}

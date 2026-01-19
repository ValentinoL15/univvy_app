package com.unnivy.unnivy_app.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class User {

    public enum RoleType {
        USER,
        ADMIN
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long user_id;
    protected String name;
    protected String lastname;
    @Column(unique = true, nullable = false)
    protected String email;
    protected String password;
    @Column(unique = true,nullable = false)
    protected String username;
    @Column(unique = true,nullable = false)
    protected String phone;
    protected LocalDate birth;
    protected String profile_photo;
    protected String university;
    @Enumerated(EnumType.STRING)
    protected RoleType role = RoleType.USER;
    protected boolean email_verification = false;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    protected List<Email_Verification> emailVerifications;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.LAZY)
    protected List<Token> token;
    protected boolean enabled = false;
    protected boolean accountNotExpired = true;
    protected boolean accountNotLocked = true;
    protected boolean credentialNotExpired = true;

    protected User() {
    }

    public User(Long user_id,String name, String lastname, String email, String password, String username, String phone, String university,RoleType role , String profile_photo,
                LocalDate birth, boolean email_verification, boolean enabled, boolean accountNotExpired, boolean accountNotLocked, boolean credentialNotExpired) {
        this.user_id = user_id;
        this.name = name;
        this.lastname = lastname;
        this.email = email;
        this.password = password;
        this.username = username;
        this.phone = phone;
        this.university = university;
        this.role = role;
        this.profile_photo = profile_photo;
        this.birth = birth;
        this.email_verification = email_verification;
        this.enabled = enabled;
        this.accountNotExpired = accountNotExpired;
        this.accountNotLocked = accountNotLocked;
        this.credentialNotExpired = credentialNotExpired;
    }
}

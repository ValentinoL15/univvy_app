package com.unnivy.unnivy_app.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "email_verification")
public class Email_Verification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_verification;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User user;

    private String code;

    private LocalDateTime expiryDate;

    private boolean revoked=false;

    public boolean isExpired() {
        return expiryDate.isBefore(LocalDateTime.now());
    }

    public Email_Verification(User user, String code, LocalDateTime expiryDate, boolean revoked) {
        this.user = user;
        this.code = code;
        this.expiryDate = expiryDate;
        this.revoked = revoked;
    }


}

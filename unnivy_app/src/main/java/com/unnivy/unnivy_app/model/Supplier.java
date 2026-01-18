package com.unnivy.unnivy_app.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "suppliers")
public class Supplier extends User{

    private String year;
    @ElementCollection
    @CollectionTable(
            name = "supplier_strengths",
            joinColumns = @JoinColumn(name = "supplier_id")
    )
    @Column(name = "strength_name")
    private List<String> strengths = new ArrayList<>();
    @ElementCollection
    @CollectionTable(
            name = "supplier_services",
            joinColumns = @JoinColumn(name = "supplier_id")
    )
    @Column(name = "service")
    private List<String> services = new ArrayList<>();
    private boolean premium = false;
    @Enumerated(EnumType.STRING)
    private Status status = Status.FREE;

    public Supplier() {
    }

    public Supplier(Long user_id,String name, String lastname, String email, String password, String username, String phone, String university,RoleType role, String profile_photo, LocalDate birth,
                    boolean email_verification, boolean enabled, boolean accountNotExpired, boolean accountNotLocked, boolean credentialNotExpired,
                    String year,List<String> strengths,List<String> services,boolean premium, Status status) {
        super(user_id,name, lastname, email, password, username, phone, university,role,
                profile_photo, birth, email_verification, enabled, accountNotExpired, accountNotLocked, credentialNotExpired
                );
        this.year = year;
        this.strengths = strengths != null ? strengths : new ArrayList<>();
        this.services = services != null ? services : new ArrayList<>();
        this.premium = premium;
        if (status != null) {
            this.status = status;
        }
    }

    public boolean isPremium() {
        return premium;
    }

    public enum Status {
        FREE,
        PENDING,
        ACTIVE,
        FAILED,
        CANCELED,
        CANCELED_PENDING
    }
}

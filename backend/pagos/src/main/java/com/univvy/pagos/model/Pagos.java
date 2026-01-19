package com.univvy.pagos.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "pagos")
public class Pagos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pago_id") // Esto le dice a la DB que busque "pago_id"
    private Long pagoId;

    @Column(name = "user_id")
    private Long userId;

    private String stripeCustomerId;
    private String stripeSubscriptionId;

    // Estado de la suscripción (active, trialing, canceled, past_due)
    private String status;

    // Plan actual
    @Enumerated(EnumType.STRING)
    private PlanType planType; // MONTHLY o ANNUAL

    private LocalDateTime currentPeriodStart;
    private LocalDateTime currentPeriodEnd;

    public boolean isActive() {
        return "active".equals(this.status) &&
                currentPeriodEnd.isAfter(LocalDateTime.now());
    }


}

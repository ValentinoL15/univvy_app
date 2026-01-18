package com.univvy.pagos.dto.PagosDTOs;

import com.univvy.pagos.model.PlanType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PagosDTO {

    private Long pago_id;

    private Long user_id;

    private String status;

    private PlanType planType; // MONTHLY o ANNUAL

    private LocalDateTime currentPeriodStart;
    private LocalDateTime currentPeriodEnd;

}

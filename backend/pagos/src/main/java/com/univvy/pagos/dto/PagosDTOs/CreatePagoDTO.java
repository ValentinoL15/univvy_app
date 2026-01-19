package com.univvy.pagos.dto.PagosDTOs;

import com.univvy.pagos.model.PlanType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreatePagoDTO {

    private String product_id;
    private Long user_id;

}

package com.unnivy.unnivy_app.dto.TokenDTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TokenDTO {

    private Long user_id;
    private String token;
    private boolean expired;
    private boolean revoked;

}

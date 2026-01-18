package com.unnivy.unnivy_app.dto.UserDTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    private Long user_id;
    private String email;
    private String username;

}

package com.unnivy.unnivy_app.dto.ClientDTOs;

import java.time.LocalDate;

public record ClientDTO(Long user_id,
                        String name,
                        String lastname,
                        String email,
                        String username,
                        String phone,
                        LocalDate birth,
                        String profile_photo,
                        String university) {
}

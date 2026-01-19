package com.unnivy.unnivy_app.dto.SupplierDTOs;

import java.time.LocalDate;
import java.util.List;

public record SupplierDTO(Long user_id,
                          String name,
                          String lastname,
                          String email,
                          String username,
                          String phone,
                          LocalDate birth,
                          String profile_photo,
                          String university,
                          String year,
                          List<String> strengths,
                          List<String> services,
                          boolean premium) {
}

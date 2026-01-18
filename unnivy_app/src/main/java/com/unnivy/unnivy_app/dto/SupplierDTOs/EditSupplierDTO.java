package com.unnivy.unnivy_app.dto.SupplierDTOs;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class EditSupplierDTO {

    @NotBlank(message = "El nombre no puede estar vacío")
    @Size(min = 3, max = 20, message = "El nombre debe contener entre 3 y 20 caracteres")
    private String name;

    @NotBlank(message = "El apellido no puede estar vacío")
    @Size(min = 3, max = 20, message = "El apellido debe contener entre 3 y 20 caracteres")
    private String lastname;

    @NotBlank(message = "El username no puede estar vacío")
    @Size(min = 5, max = 20, message = "El username debe contener entre 5 y 20 caracteres")
    private String username;
    @NotNull(message = "La fecha de nacimiento no puede ser nula")
    private LocalDate birth;
    @NotNull(message = "Por favor elige una foto de perfil")
    private String profile_photo;
    @NotBlank(message = "Elige a la universidad que has ido")
    private String university;

    @NotBlank(message = "El año no puede estar vacío")
    private String year;

    @NotEmpty(message = "Debes agregar al menos una fortaleza")
    private List<String> strengths;

    @NotBlank(message = "Debes describir tus servicios")
    private List<String> services;

}

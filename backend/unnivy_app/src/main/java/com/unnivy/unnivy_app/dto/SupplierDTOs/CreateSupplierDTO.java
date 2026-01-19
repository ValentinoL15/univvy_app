package com.unnivy.unnivy_app.dto.SupplierDTOs;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class CreateSupplierDTO {

    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

    // Tu regex de contraseña
    private static final String PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[^a-zA-Z0-9]).{8,}$";

    @NotBlank(message = "El nombre no puede estar vacío")
    @Size(min = 3, max = 20, message = "El nombre debe contener entre 3 y 20 caracteres")
    private String name;

    @NotBlank(message = "El apellido no puede estar vacío")
    @Size(min = 3, max = 20, message = "El apellido debe contener entre 3 y 20 caracteres")
    private String lastname;

    @NotBlank(message = "El email no puede estar vacío")
    @Pattern(regexp = EMAIL_REGEX, message = "El formato del email no es válido (ej. usuario@dominio.com)")
    private String email;

    @NotBlank(message = "La contraseña no puede estar vacía")
    @Size(min = 8, max = 20, message = "La contraseña debe contener entre 8 y 20 caracteres")
    @Pattern(
            regexp = PASSWORD_REGEX,
            message = "La contraseña debe contener al menos 8 caracteres, 1 mayúscula, 1 minúscula, y 1 carácter especial"
    )
    private String password;

    @NotBlank(message = "El username no puede estar vacío")
    @Size(min = 5, max = 20, message = "El username debe contener entre 5 y 20 caracteres")
    private String username;
    private String phone;
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

    @NotEmpty(message = "Debes agregar al menos un servicio")
    private List<String> services;

    private boolean premium = false;

}

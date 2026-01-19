package com.unnivy.unnivy_app.dto.AuthReponsesDTOs;

import jakarta.validation.constraints.NotBlank;

public record LoginDTO(
        @NotBlank(message = "El username no puede estar vacío")
        String username,

        @NotBlank(message = "La contraseña no puede estar vacía")
        String password
) {}

package com.unnivy.unnivy_app.dto.CommentsDTOs;

import com.unnivy.unnivy_app.model.Client;
import com.unnivy.unnivy_app.model.Supplier;
import jakarta.validation.constraints.*;

public record CreateCommentDTO(
        @NotBlank(message = "La descripción no puede estar vacía")
        @Size(min = 10, message = "La descripción debe tener al menos 10 caracteres")
        String description,

        @NotNull(message = "El valor es obligatorio")
        @Min(value = 1, message = "El valor mínimo es 1")
        @Max(value = 5, message = "El valor máximo es 5")
        Integer value,

        @NotNull(message = "El ID del emisor es obligatorio")
        Long from,

        @NotNull(message = "El ID del receptor es obligatorio")
        Long to
) {
}

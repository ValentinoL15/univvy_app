package com.unnivy.unnivy_app.dto.CommentsDTOs;

import com.unnivy.unnivy_app.model.Client;
import com.unnivy.unnivy_app.model.Supplier;

public record CommentDTO(Long comment_id,
                         String description,
                         Integer value,
                         Long client_id,
                         String name_client,
                         Long supplier_id,
                         String name_supplier
                         ) {
}

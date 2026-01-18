package com.unnivy.unnivy_app.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String roomId;    // Ejemplo: "p10-c25" (proveedor 10, cliente 25)
    private String sender;    // Nombre de usuario o ID
    private String content;
    private LocalDateTime timestamp;
}
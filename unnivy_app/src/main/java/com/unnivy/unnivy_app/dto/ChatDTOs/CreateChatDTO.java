package com.unnivy.unnivy_app.dto.ChatDTOs;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CreateChatDTO {

    String roomId;
    String sender;
    String content;
    LocalDateTime timestamp;

}

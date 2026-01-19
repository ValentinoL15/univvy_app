package com.unnivy.unnivy_app.dto.ChatDTOs;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChatMessageDTO {

    Long id;
    String roomId;
    String message;
    String user;

}

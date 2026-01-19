package com.unnivy.unnivy_app.service.ServicesInterfaces;

import com.unnivy.unnivy_app.dto.ChatDTOs.CreateChatDTO;
import com.unnivy.unnivy_app.model.ChatMessage;

import java.util.List;

public interface IChatService {

    public List<ChatMessage> getChat(String roomId, String currentUser);

    public CreateChatDTO createMessage(String roomId, CreateChatDTO chatDTO, String sender);

    public String getChatRoomId(String userId1, String userId2);
}

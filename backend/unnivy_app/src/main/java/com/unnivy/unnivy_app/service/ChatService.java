package com.unnivy.unnivy_app.service;

import com.unnivy.unnivy_app.dto.ChatDTOs.CreateChatDTO;
import com.unnivy.unnivy_app.model.ChatMessage;
import com.unnivy.unnivy_app.model.User;
import com.unnivy.unnivy_app.repository.IChatRepository;
import com.unnivy.unnivy_app.repository.IUserRepository;
import com.unnivy.unnivy_app.service.ServicesInterfaces.IChatService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService implements IChatService {

    private final IChatRepository chatRepository;
    private final IUserRepository userRepository;

    @Override
    public List<ChatMessage> getChat(String roomId, String currentUser) {
        String[] authorizedUsers = roomId.split("_");
        boolean isAuthorized = false;
        for (String username : authorizedUsers) {
            if (username.equals(currentUser)) {
                isAuthorized = true;
                break;
            }
        }

        // 4. Si no está en el roomId, lanzar excepción de seguridad
        if (!isAuthorized) {
            throw new RuntimeException("No tienes permiso para ver el historial de este chat");
        }

        // 5. Si todo está bien, retornar los mensajes
        return chatRepository.findByRoomIdOrderByTimestampAsc(roomId);
    }

    @Override
    @Transactional
    public CreateChatDTO createMessage(String roomId, CreateChatDTO chatDTO, String sender) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setRoomId(roomId);
        chatMessage.setContent(chatDTO.getContent());
        chatMessage.setSender(sender);
        chatMessage.setTimestamp(LocalDateTime.now());
        chatRepository.save(chatMessage);
        chatDTO.setSender(sender);
        return chatDTO;
    }

    @Override
    public String getChatRoomId(String userId1, String userId2) {
        if (userId1.compareTo(userId2) < 0) {
            return userId1 + "_" + userId2;
        } else {
            return userId2 + "_" + userId1;
        }
    }
}

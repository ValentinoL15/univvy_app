package com.unnivy.unnivy_app.controller;

import com.unnivy.unnivy_app.dto.ChatDTOs.CreateChatDTO;
import com.unnivy.unnivy_app.model.ChatMessage;
import com.unnivy.unnivy_app.service.ServicesInterfaces.IChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class WebSocketController {

    private final IChatService chatService;

    @MessageMapping("/chat/{roomId}")
    @SendTo("/topic/{roomId}")
    public CreateChatDTO chat(@DestinationVariable String roomId, @RequestBody CreateChatDTO chatMessageDTO, Principal principal){
        return chatService.createMessage(roomId,chatMessageDTO,principal.getName());
    }

    @GetMapping("/api/chat/{roomId}")
    public ResponseEntity<List<ChatMessage>> getMessages(@PathVariable String roomId, Principal principal) {
        return ResponseEntity.ok(chatService.getChat(roomId, principal.getName()));
    }

    @GetMapping("/api/chat/room")
    public ResponseEntity<String> getChatRoomId(@PathVariable String user1,
                                                @PathVariable String user2){
        return ResponseEntity.ok(chatService.getChatRoomId(user1,user2));
    }

}
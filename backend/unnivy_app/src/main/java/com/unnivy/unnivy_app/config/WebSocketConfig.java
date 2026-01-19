package com.unnivy.unnivy_app.config;

import com.unnivy.unnivy_app.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.security.Principal;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtUtils jwtService;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*") // Permite la conexión desde Postman
                .withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

                // 1. LOGIN: Validar el token al conectar (Lo que ya tenías)
                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    String authHeader = accessor.getFirstNativeHeader("Authorization");
                    if (authHeader != null && authHeader.startsWith("Bearer ")) {
                        String token = authHeader.substring(7);
                        UsernamePasswordAuthenticationToken auth = jwtService.getAuthentication(token);
                        if (auth != null) {
                            accessor.setUser(auth);
                        }
                    }
                }

                // 2. SEGURIDAD: Validar si puede unirse a la sala
                if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
                    Principal principal = accessor.getUser();
                    String destination = accessor.getDestination(); // Ejemplo: "/topic/3_4"

                    if (principal == null) {
                        throw new IllegalArgumentException("No autorizado: Debes estar logueado.");
                    }

                    if (destination != null && destination.startsWith("/topic/")) {
                        String roomId = destination.replace("/topic/", "");
                        String username = principal.getName();

                        // Validamos si el usuario que intenta entrar es el 3 o el 4
                        if (!isUserAuthorizedForRoom(username, roomId)) {
                            throw new IllegalArgumentException("No tienes permiso para entrar a esta sala.");
                        }
                    }
                }
                return message;
            }
        });
    }

    // Lógica para verificar los integrantes del roomId
    private boolean isUserAuthorizedForRoom(String username, String roomId) {
        // Si el username es "Valen15" y la sala es "Pepe_Valen15"
        // El split genera ["Pepe", "Valen15"]
        String[] authorizedUsers = roomId.split("_");

        for (String authorizedUser : authorizedUsers) {
            // Comparamos los nombres de usuario directamente
            if (authorizedUser.equals(username)) {
                return true;
            }
        }
        return false;
    }
}

package com.unnivy.unnivy_app.dto.AuthReponsesDTOs;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AuthResponse(String message,
                           @JsonProperty("access_token") String access_token,
                           @JsonProperty("refresh_token") String refresh_token) {
}

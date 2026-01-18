package com.unnivy.unnivy_app.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TokenResponse(@JsonProperty("access_token") String access_token,
                            @JsonProperty("refresh_token") String refresh_token) {
}

package com.unnivy.unnivy_app.dto;

import lombok.*;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GeneralResponse {

    private Date timestamp = new Date();
    private String message;
    private int status;

}

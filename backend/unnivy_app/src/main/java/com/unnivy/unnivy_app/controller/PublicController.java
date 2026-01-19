package com.unnivy.unnivy_app.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/public")
public class PublicController {

    @PreAuthorize("hasRole('USER')")
    @GetMapping
    public String hola() {
        return "Hola";
    }

}

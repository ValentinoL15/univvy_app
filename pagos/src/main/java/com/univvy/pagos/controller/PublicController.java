package com.univvy.pagos.controller;

import com.univvy.pagos.dto.TokenDTOs.TokenDTO;
import com.univvy.pagos.dto.UserDTOs.UserDTO;
import com.univvy.pagos.repository.IUserApi;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

    @RestController
    @RequiredArgsConstructor
    public class PublicController {

        private final IUserApi userApi;

        @GetMapping("/api/{user_id}")
        public String getPublic(@PathVariable Long user_id){
            UserDTO userDTO = userApi.getUser(user_id);
            System.out.println(userDTO);
            return "Hola mundo";
        }


    }

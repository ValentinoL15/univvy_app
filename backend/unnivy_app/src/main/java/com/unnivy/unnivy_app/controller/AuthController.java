package com.unnivy.unnivy_app.controller;

import com.unnivy.unnivy_app.dto.AuthReponsesDTOs.AuthResponse;
import com.unnivy.unnivy_app.dto.AuthReponsesDTOs.ErrorResponseDTO;
import com.unnivy.unnivy_app.dto.ClientDTOs.SaveClientDTO;
import com.unnivy.unnivy_app.dto.AuthReponsesDTOs.LoginDTO;
import com.unnivy.unnivy_app.dto.GeneralResponse;
import com.unnivy.unnivy_app.dto.SupplierDTOs.CreateSupplierDTO;
import com.unnivy.unnivy_app.dto.TokenResponse;
import com.unnivy.unnivy_app.dto.UserDTOs.UserDto;
import com.unnivy.unnivy_app.exceptions.exceptionsHandler.EmailNotVerifiedException;
import com.unnivy.unnivy_app.service.ServicesInterfaces.IClientService;
import com.unnivy.unnivy_app.service.ServicesInterfaces.ISupplierService;
import com.unnivy.unnivy_app.service.ServicesInterfaces.IUserService;
import com.unnivy.unnivy_app.service.UserSeriviceImp;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final IClientService clientService;
    private final ISupplierService supplierService;
    private final UserSeriviceImp userSeriviceImp;

    @PostMapping("/registerClient")
    public ResponseEntity<GeneralResponse> registerClient(@Valid @RequestBody SaveClientDTO clientDTO) {
        return ResponseEntity.ok(clientService.saveClient(clientDTO));
    }

    @PostMapping("/registerSupplier")
    public ResponseEntity<GeneralResponse> registerSupplier(@Valid @RequestBody CreateSupplierDTO supplierDTO) {
        return ResponseEntity.ok(supplierService.saveSupplier(supplierDTO));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginDTO loginDTO) {
        return ResponseEntity.ok(userSeriviceImp.login(loginDTO));
    }

    @PostMapping("/refresh")
    public TokenResponse refreshToken(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        return userSeriviceImp.refreshToken(authHeader);
    }

    @GetMapping("/user/{user_id}")
    public ResponseEntity<UserDto> getUser(@PathVariable Long user_id) {
        return ResponseEntity.ok(userSeriviceImp.getUser(user_id));
    }
}


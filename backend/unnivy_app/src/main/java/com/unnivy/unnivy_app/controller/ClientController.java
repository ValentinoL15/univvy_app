package com.unnivy.unnivy_app.controller;

import com.unnivy.unnivy_app.dto.ClientDTOs.ClientDTO;
import com.unnivy.unnivy_app.dto.ClientDTOs.EditClientDTO;
import com.unnivy.unnivy_app.dto.ClientDTOs.SaveClientDTO;
import com.unnivy.unnivy_app.dto.GeneralResponse;
import com.unnivy.unnivy_app.service.ServicesInterfaces.IClientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class ClientController {

    private final IClientService clientService;

    @GetMapping("/{user_id}")
    public ResponseEntity<ClientDTO> getClient(@PathVariable Long user_id) {
        return ResponseEntity.ok(clientService.getClientId(user_id));
    }

    @GetMapping
    public ResponseEntity<List<ClientDTO>> getClients(){
        return ResponseEntity.ok(clientService.getAllClients());
    }

    @PutMapping("/edit/{user_id}")
    public ResponseEntity<GeneralResponse> editClient(@PathVariable Long user_id,
                                                      @RequestBody EditClientDTO clientDTO,
                                                      Principal principal){
        return ResponseEntity.ok(clientService.updateClient(user_id,clientDTO,principal.getName()));
    }

    @DeleteMapping("/delete/{user_id}")
    public ResponseEntity<Void> deleteClient(@PathVariable Long user_id,
                                             Principal principal){
        clientService.deleteClient(user_id,principal.getName());
        return ResponseEntity.ok().build();
    }

}

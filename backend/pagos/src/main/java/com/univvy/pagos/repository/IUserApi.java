package com.univvy.pagos.repository;

import com.univvy.pagos.config.FeignConfig;
import com.univvy.pagos.dto.TokenDTOs.TokenDTO;
import com.univvy.pagos.dto.UserDTOs.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "univvy-app", configuration = FeignConfig.class)
public interface IUserApi {

    @GetMapping("/api/auth/user/{user_id}")
    public UserDTO getUser(@PathVariable Long user_id);

    @GetMapping("/api/token")
    public TokenDTO getToken(@RequestHeader("Authorization") String authHeader);

    @PutMapping("/api/suppliers/editPremium/{status}/{username}")
    public void editSupplierPremium(@PathVariable String status, @PathVariable String username);
}

package com.unnivy.unnivy_app.controller;

import com.unnivy.unnivy_app.dto.GeneralResponse;
import com.unnivy.unnivy_app.dto.SupplierDTOs.EditSupplierDTO;
import com.unnivy.unnivy_app.dto.SupplierDTOs.SupplierDTO;
import com.unnivy.unnivy_app.model.Supplier;
import com.unnivy.unnivy_app.service.ServicesInterfaces.ISupplierService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/suppliers")
@RequiredArgsConstructor
public class SupplierController {

    private final ISupplierService supplierService;

    @GetMapping
    public ResponseEntity<List<SupplierDTO>> getSuppliers(){
        return ResponseEntity.ok(supplierService.getAllSuppliers());
    }

    @GetMapping("/{user_id}")
    public ResponseEntity<SupplierDTO> getSupplier(@PathVariable Long user_id){
        return ResponseEntity.ok(supplierService.getSupplierId(user_id));
    }

    @PutMapping("/edit/{user_id}")
    public ResponseEntity<GeneralResponse> editSupplier(@PathVariable Long user_id,
                                                        @RequestBody @Valid EditSupplierDTO supplierDTO,
                                                        Principal principal
                                                        ) {
        return ResponseEntity.ok(supplierService.updateSupplier(user_id,supplierDTO,principal.getName()));
    }

    @DeleteMapping("/delete/{user_id}")
    public ResponseEntity<Void> deleteSupplier(@PathVariable Long user_id,
                                               Principal principal) {
        supplierService.deleteSupplier(user_id,principal.getName());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/editPremium/{status}/{username}")
    public ResponseEntity<Void> editSupplierPremium(@PathVariable Supplier.Status status, @PathVariable String username){
        supplierService.updatePremiumSupplier(status,username);
        return ResponseEntity.noContent().build();
    }

}

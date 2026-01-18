package com.unnivy.unnivy_app.service.ServicesInterfaces;

import com.unnivy.unnivy_app.dto.GeneralResponse;
import com.unnivy.unnivy_app.dto.SupplierDTOs.CreateSupplierDTO;
import com.unnivy.unnivy_app.dto.SupplierDTOs.EditSupplierDTO;
import com.unnivy.unnivy_app.dto.SupplierDTOs.SupplierDTO;
import com.unnivy.unnivy_app.model.Supplier;

import java.util.List;

public interface ISupplierService {

    public List<SupplierDTO> getAllSuppliers();

    public SupplierDTO getSupplierId(Long supplier_id);

    public GeneralResponse saveSupplier(CreateSupplierDTO supplierDTO);

    public GeneralResponse updateSupplier(Long supplier_id, EditSupplierDTO supplierDTO, String currentUser);

    public void deleteSupplier(Long user_id, String currentUser);

    public String encryptPassword(String password);

    public void updatePremiumSupplier(Supplier.Status status, String username);


}

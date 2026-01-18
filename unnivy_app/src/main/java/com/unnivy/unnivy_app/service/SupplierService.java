package com.unnivy.unnivy_app.service;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.unnivy.unnivy_app.dto.EmailDTOs.EmailRequest;
import com.unnivy.unnivy_app.dto.GeneralResponse;
import com.unnivy.unnivy_app.dto.SupplierDTOs.CreateSupplierDTO;
import com.unnivy.unnivy_app.dto.SupplierDTOs.EditSupplierDTO;
import com.unnivy.unnivy_app.dto.SupplierDTOs.SupplierDTO;
import com.unnivy.unnivy_app.dto.TokenResponse;
import com.unnivy.unnivy_app.exceptions.exceptionsHandler.AccessDeniedException;
import com.unnivy.unnivy_app.model.Supplier;
import com.unnivy.unnivy_app.model.Token;
import com.unnivy.unnivy_app.model.User;
import com.unnivy.unnivy_app.repository.ISupplierRepository;
import com.unnivy.unnivy_app.repository.ITokenRepository;
import com.unnivy.unnivy_app.repository.IUserRepository;
import com.unnivy.unnivy_app.service.ServicesInterfaces.IEmailVerificationService;
import com.unnivy.unnivy_app.service.ServicesInterfaces.ISupplierService;
import com.unnivy.unnivy_app.service.ServicesInterfaces.IUserService;
import com.unnivy.unnivy_app.utils.JwtUtils;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SupplierService implements ISupplierService {

    final private ISupplierRepository supplierRepository;
    final private IUserRepository userRepository;
    final private ITokenRepository tokenRepository;
    private final JwtUtils jwtUtils;
    private final IEmailVerificationService emailVerificationService;

    // Función para validar teléfono de España
    public boolean validarTelefono(String numero, String codigoPais) {
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();

        try {
            // 1. Parsear el número: Lo convierte a un objeto interno de la librería
            Phonenumber.PhoneNumber phoneNumber = phoneUtil.parse(numero, codigoPais);

            // 2. Validar: Comprueba si es un número válido para la región especificada
            boolean isValid = phoneUtil.isValidNumberForRegion(phoneNumber, codigoPais);

            return isValid;

        } catch (Exception e) {
            // Captura errores de parseo (por ejemplo, si el número es muy corto o largo)
            return false;
        }
    }

    @Override
    public List<SupplierDTO> getAllSuppliers() {
        List<Supplier> suppliers = supplierRepository.findAll();
        return  suppliers.stream().map(
                supplier -> new SupplierDTO(
                        supplier.getUser_id(),supplier.getName(),supplier.getLastname(),supplier.getEmail(),supplier.getUsername(),supplier.getPhone(),supplier.getBirth(),
                        supplier.getProfile_photo(),supplier.getUniversity(),supplier.getYear(),supplier.getStrengths(),supplier.getServices(),supplier.isPremium()
                )
        ).toList();
    }

    @Override
    public SupplierDTO getSupplierId(Long supplier_id) {
        Supplier supplier = supplierRepository.findById(supplier_id)
                .orElseThrow(() -> new EntityNotFoundException("Proveedor no encontrado con id: " + supplier_id));
        SupplierDTO supplierDTO = new SupplierDTO(
                supplier.getUser_id(),supplier.getName(),supplier.getLastname(),supplier.getEmail(),supplier.getUsername(),supplier.getPhone(),supplier.getBirth(),
                supplier.getProfile_photo(),supplier.getUniversity(),supplier.getYear(),supplier.getStrengths(),supplier.getServices(),supplier.isPremium()
        );
        return supplierDTO;
    }

    @Override
    @Transactional
    public GeneralResponse saveSupplier(CreateSupplierDTO supplierDTO) {
        if(userRepository.existsByEmail(supplierDTO.getEmail())){
            throw new UsernameNotFoundException("El email ya está registrado, por favor elige otro");
        }
        if(userRepository.existsByUsername(supplierDTO.getUsername())){
            throw new UsernameNotFoundException("EL username ya está en uso, por favor prueba con otro");
        }
        Supplier supplier = new Supplier();
        supplier.setName(supplierDTO.getName());
        supplier.setLastname(supplierDTO.getLastname());
        supplier.setEmail(supplierDTO.getEmail());
        supplier.setPassword(encryptPassword(supplierDTO.getPassword()));
        supplier.setUsername(supplierDTO.getUsername());
        if(supplierDTO.getPhone() != null && !supplierDTO.getPhone().isEmpty()) {

            final String CODIGO_PAIS = "ES";
            if (!validarTelefono(supplierDTO.getPhone(), CODIGO_PAIS)) {
                throw new RuntimeException("El número de teléfono no es un número válido para " + CODIGO_PAIS);
            }

        }
        supplier.setPhone(supplierDTO.getPhone());
        supplier.setBirth(supplierDTO.getBirth());
        supplier.setProfile_photo(supplierDTO.getProfile_photo());
        supplier.setUniversity(supplierDTO.getUniversity());
        supplier.setYear(supplierDTO.getYear());
        supplier.setStrengths(supplierDTO.getStrengths());
        supplier.setServices(supplierDTO.getServices());
        supplier.setPremium(false);
        Supplier savedSupplier = supplierRepository.save(supplier);

        EmailRequest request = new EmailRequest(savedSupplier.getEmail());
        emailVerificationService.createCodeVerification(request);

        return new GeneralResponse(
                new Date(),
                "Proveedor creado con éxito",
                HttpStatus.CREATED.value()
        );

    }

    @Override
    @Transactional
    public GeneralResponse updateSupplier(Long supplier_id, EditSupplierDTO supplierDTO, String currentUser) {
        Supplier supplier = supplierRepository.findById(supplier_id)
                .orElseThrow(() -> new RuntimeException("EL proveedor no se encuentra"));
        if(!supplier.getUsername().equals(currentUser)){
            throw new AccessDeniedException();
        }
        if (supplierDTO.getName() != null) supplier.setName(supplierDTO.getName());
        if (supplierDTO.getLastname() != null) supplier.setLastname(supplierDTO.getLastname());
        if (supplierDTO.getBirth() != null) supplier.setBirth(supplierDTO.getBirth());
        if (supplierDTO.getProfile_photo() != null) supplier.setProfile_photo(supplierDTO.getProfile_photo());
        if (supplierDTO.getUniversity() != null) supplier.setUniversity(supplierDTO.getUniversity());
        if (supplierDTO.getYear() != null) supplier.setYear(supplierDTO.getYear());
        if (supplierDTO.getStrengths() != null) supplier.setStrengths(supplierDTO.getStrengths());
        if (supplierDTO.getServices() != null) supplier.setServices(supplierDTO.getServices());
        if (supplierDTO.getUsername() != null && !supplier.getUsername().equals(supplierDTO.getUsername())) {
            if (userRepository.existsByUsername(supplierDTO.getUsername())) {
                throw new RuntimeException("El nombre de usuario ya está en uso");
            }
            supplier.setUsername(supplierDTO.getUsername());
        }

        supplierRepository.save(supplier);

        return new GeneralResponse(
                new Date(),
                "Proveedor actualizado con éxito",
                HttpStatus.OK.value()
        );
    }

    @Override
    @Transactional
    public void deleteSupplier(Long user_id, String currentUser) {
        Supplier supplier = supplierRepository.getReferenceById(user_id);
        if(!supplier.getUsername().equals(currentUser)){
            throw new AccessDeniedException();
        }
        supplierRepository.deleteById(user_id);
    }

    @Override
    public String encryptPassword(String password) {
        return new BCryptPasswordEncoder().encode(password);
    }

    @Override
    @Transactional
    public void updatePremiumSupplier(Supplier.Status status, String username) {
        Supplier supplier = supplierRepository.findSupplierEntityByUsername(username)
                .orElseThrow(() -> new RuntimeException("El proveedor no se encuentra"));

        supplier.setStatus(status);
        switch (status){
            case ACTIVE,CANCELED_PENDING -> supplier.setPremium(true);
            default -> supplier.setPremium(false);
        }
        supplierRepository.save(supplier);
    }


}

package com.unnivy.unnivy_app.service;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.unnivy.unnivy_app.dto.ClientDTOs.ClientDTO;
import com.unnivy.unnivy_app.dto.ClientDTOs.EditClientDTO;
import com.unnivy.unnivy_app.dto.ClientDTOs.SaveClientDTO;
import com.unnivy.unnivy_app.dto.EmailDTOs.EmailRequest;
import com.unnivy.unnivy_app.dto.GeneralResponse;
import com.unnivy.unnivy_app.dto.TokenResponse;
import com.unnivy.unnivy_app.exceptions.exceptionsHandler.AccessDeniedException;
import com.unnivy.unnivy_app.model.Client;
import com.unnivy.unnivy_app.model.Token;
import com.unnivy.unnivy_app.model.User;
import com.unnivy.unnivy_app.repository.IClientRepository;
import com.unnivy.unnivy_app.repository.IEmailVerificationRepository;
import com.unnivy.unnivy_app.repository.ITokenRepository;
import com.unnivy.unnivy_app.service.ServicesInterfaces.IClientService;
import com.unnivy.unnivy_app.repository.IUserRepository;
import com.unnivy.unnivy_app.service.ServicesInterfaces.IEmailVerificationService;
import com.unnivy.unnivy_app.service.ServicesInterfaces.IUserService;
import com.unnivy.unnivy_app.utils.JwtUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClientService implements IClientService {

    private final IClientRepository clientRepository;
    private final IUserRepository userRepository;
    private final ITokenRepository tokenRepository;
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
    public List<ClientDTO> getAllClients() {
        List<Client> clients = clientRepository.findAll();
        List<ClientDTO> clientsDto = clients.stream().map(
                client -> new ClientDTO(client.getUser_id(),client.getName(),client.getLastname(),client.getEmail(),client.getUsername()
                        ,client.getPhone(),client.getBirth(),client.getProfile_photo(),client.getUniversity())
        ).collect(Collectors.toList());
        return clientsDto;
    }

    @Override
    public ClientDTO getClientId(Long client_id) {
        Client client = clientRepository.findById(client_id)
                .orElseThrow(() -> new RuntimeException("El cliente no se encuentra"));
        ClientDTO clientDTO = new ClientDTO(
                client.getUser_id(),
                client.getName(),client.getLastname(),client.getEmail(),client.getUsername(),client.getPhone(),
                client.getBirth(),client.getProfile_photo(),client.getUniversity()
        );
        return clientDTO;
    }

    @Override
    @Transactional
    public GeneralResponse saveClient(SaveClientDTO clientDTO) {

        if(userRepository.existsByEmail(clientDTO.getEmail())) {
            throw new UsernameNotFoundException("El email ya está registrado, por favor elige otro");
        }
        if(userRepository.existsByUsername(clientDTO.getUsername())) {
            throw new UsernameNotFoundException("EL username ya está en uso, por favor prueba con otro");
        }

        Client client = new Client();
        client.setName(clientDTO.getName());
        client.setLastname(clientDTO.getLastname());
        client.setEmail(clientDTO.getEmail());

        client.setPassword(encryptPassword(clientDTO.getPassword()));
        client.setUsername(clientDTO.getUsername());
        if(clientDTO.getPhone() != null && !clientDTO.getPhone().isEmpty()) {

            final String CODIGO_PAIS = "ES";
            if (!validarTelefono(clientDTO.getPhone(), CODIGO_PAIS)) {
                throw new RuntimeException("El número de teléfono no es un número válido para " + CODIGO_PAIS);
            }

        }
        client.setPhone(clientDTO.getPhone());
        client.setBirth(clientDTO.getBirth());
        client.setProfile_photo(clientDTO.getProfile_photo());
        client.setUniversity(clientDTO.getUniversity());
        Client savedUser = clientRepository.save(client);
        EmailRequest request = new EmailRequest(savedUser.getEmail());
        emailVerificationService.createCodeVerification(request);
        return new GeneralResponse(new Date(),
                "Usuario creado con éxito",
                HttpStatus.CREATED.value());
    }

    @Override
    @Transactional
    public GeneralResponse updateClient(Long client_id, EditClientDTO clientDTO, String currentUser) {

        Client client = clientRepository.findById(client_id)
                .orElseThrow(() -> new RuntimeException("El cliente no ha sido encontrado"));

        if(!client.getUsername().equals(currentUser)){
            throw new AccessDeniedException();
        }

        // Actualización condicional: Solo si el DTO trae un valor nuevo
        if (clientDTO.getName() != null) client.setName(clientDTO.getName());
        if (clientDTO.getLastname() != null) client.setLastname(clientDTO.getLastname());
        if (clientDTO.getBirth() != null) client.setBirth(clientDTO.getBirth());
        if (clientDTO.getProfile_photo() != null) client.setProfile_photo(clientDTO.getProfile_photo());
        if (clientDTO.getUniversity() != null) client.setUniversity(clientDTO.getUniversity());
        if (clientDTO.getUsername() != null && !client.getUsername().equals(clientDTO.getUsername())) {
            if(userRepository.existsByUsername(clientDTO.getUsername())) {
                throw new RuntimeException("El username ya está en uso");
            }
            client.setUsername(clientDTO.getUsername());
        }
        clientRepository.save(client);
        return new GeneralResponse(
                new Date(),
                "Cliente editado con éxito",
                HttpStatus.OK.value()
        );
    }

    @Override
    @Transactional
    public void deleteClient(Long id_client, String currentUser) {
        Client client = clientRepository.getReferenceById(id_client);
        if(!client.getUsername().equals(currentUser)){
            throw new AccessDeniedException();
        }
        clientRepository.deleteById(id_client);
    }

    @Override
    public String encryptPassword(String password) {
        return new BCryptPasswordEncoder().encode(password);
    }





}

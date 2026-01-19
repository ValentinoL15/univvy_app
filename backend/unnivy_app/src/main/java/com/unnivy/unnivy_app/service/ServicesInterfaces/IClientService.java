package com.unnivy.unnivy_app.service.ServicesInterfaces;

import com.unnivy.unnivy_app.dto.ClientDTOs.ClientDTO;
import com.unnivy.unnivy_app.dto.ClientDTOs.EditClientDTO;
import com.unnivy.unnivy_app.dto.ClientDTOs.SaveClientDTO;
import com.unnivy.unnivy_app.dto.GeneralResponse;
import com.unnivy.unnivy_app.dto.TokenResponse;
import com.unnivy.unnivy_app.model.User;

import java.util.List;

public interface IClientService {

    public List<ClientDTO> getAllClients();

    public ClientDTO getClientId(Long client_id);

    public GeneralResponse saveClient(SaveClientDTO clientDTO);

    public GeneralResponse updateClient(Long client_id, EditClientDTO clientDTO, String currentUser);

    public void deleteClient(Long id_client, String currentUser);

    public String encryptPassword(String password);






}

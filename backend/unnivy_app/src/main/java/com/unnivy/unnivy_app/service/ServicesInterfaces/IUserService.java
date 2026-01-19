package com.unnivy.unnivy_app.service.ServicesInterfaces;

import com.unnivy.unnivy_app.dto.TokenResponse;
import com.unnivy.unnivy_app.dto.UserDTOs.UserDto;
import com.unnivy.unnivy_app.model.User;

public interface IUserService {

    public String encryptPassword(String password);

    public void savedUserToken(User user, String jwtToken);

    public TokenResponse refreshToken(final String authHeader);


}

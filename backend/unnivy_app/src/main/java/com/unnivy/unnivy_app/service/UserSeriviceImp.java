package com.unnivy.unnivy_app.service;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.unnivy.unnivy_app.dto.AuthReponsesDTOs.AuthResponse;
import com.unnivy.unnivy_app.dto.AuthReponsesDTOs.LoginDTO;
import com.unnivy.unnivy_app.dto.TokenResponse;
import com.unnivy.unnivy_app.dto.UserDTOs.UserDto;
import com.unnivy.unnivy_app.exceptions.exceptionsHandler.EmailNotVerifiedException;
import com.unnivy.unnivy_app.model.Token;
import com.unnivy.unnivy_app.model.User;
import com.unnivy.unnivy_app.repository.ITokenRepository;
import com.unnivy.unnivy_app.repository.IUserRepository;
import com.unnivy.unnivy_app.service.ServicesInterfaces.IUserService;
import com.unnivy.unnivy_app.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserSeriviceImp implements UserDetailsService {

    private final IUserRepository userRepository;
    private final JwtUtils jwtUtils;
    private final ITokenRepository tokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("No se encuentra el username"));

        List<SimpleGrantedAuthority> authorityList = new ArrayList<>();

        String roleName = user.getRole().name();
        authorityList.add(new SimpleGrantedAuthority("ROLE_".concat(roleName)));

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.isEnabled(),
                user.isAccountNotExpired(),
                user.isCredentialNotExpired(),
                user.isAccountNotLocked(),
                authorityList);
    }

    public Authentication authenticate(String username, String password) {
        UserDetails userDetails = this.loadUserByUsername(username);

        if(userDetails == null) {
            throw new BadCredentialsException("Invalid username o password");
        }
        if(!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new BadCredentialsException("Invalid password");
        }

        return new UsernamePasswordAuthenticationToken(username, userDetails.getPassword(), userDetails.getAuthorities());

    }

    public AuthResponse login(LoginDTO loginDTO) {
        String username = loginDTO.username();
        String password = loginDTO.password();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("No se encontró el username"));

        if (!user.isEmail_verification()){
            throw new EmailNotVerifiedException("El email del usuario no ha sido verificado.");
        }

        Authentication authentication = this.authenticate(username,password);
        SecurityContextHolder.getContext().setAuthentication(authentication);


        String access_token = jwtUtils.generateToken(user);
        String refreshToken = jwtUtils.generateRefreshToken(user);

        savedUserToken(user,access_token);

        AuthResponse authResponse = new AuthResponse("Logueado correctamente",access_token,refreshToken);
        return authResponse;
    }

    public void savedUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(Token.TokenType.BEARER)
                .revoked(false)
                .expired(false)
                .build();
        tokenRepository.save(token);
    }

    public TokenResponse refreshToken(String authHeader) {

        if(authHeader == null || !authHeader.startsWith("Bearer ")){
            throw new IllegalArgumentException("Invalid Bearer token");
        }

        final String refreshToken = authHeader.substring(7);

        DecodedJWT decodedJWT = jwtUtils.validateToken(refreshToken);
        final String userName = jwtUtils.extractUsername(decodedJWT);

        if (userName == null) {
            throw new IllegalArgumentException("Invalid refresh token");
        }

        final User user = userRepository.findByUsername(userName)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + userName));

        if (!jwtUtils.isTokenValid(refreshToken, user)) {
            throw new IllegalArgumentException("Invalid refresh token");
        }

        final String accessToken = jwtUtils.generateToken(user);

        // (Opcional) Revocar tokens anteriores
        revokeAllUsersTokens(user);
        this.savedUserToken(user, accessToken);

        // Devolvemos ambos tokens
        return new TokenResponse(accessToken, refreshToken);

    }

    public void revokeAllUsersTokens(User user) {
        List<Token> validUserTokens = tokenRepository.findAllValidTokensByUser(user.getUser_id());

        if (validUserTokens.isEmpty()) return;

        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });

        tokenRepository.saveAll(validUserTokens);

    }

    public UserDto getUser(Long user_id) {
        User user = userRepository.findById(user_id)
                .orElseThrow(() -> new RuntimeException("No se encuentra el usuario"));
        UserDto userDto = new UserDto(
                user.getUser_id(),
                user.getEmail(),
                user.getUsername()
        );
        return userDto;
    }

    public User getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("No se encuentra el usuario"));
        return user;
    }


}

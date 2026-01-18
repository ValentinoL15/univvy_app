package com.unnivy.unnivy_app.security;

import com.unnivy.unnivy_app.model.Token;
import com.unnivy.unnivy_app.repository.ITokenRepository;
import com.unnivy.unnivy_app.security.filter.JwtTokenValidator;
import com.unnivy.unnivy_app.utils.CustomAccessDeniedHandler;
import com.unnivy.unnivy_app.utils.CustomAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final ITokenRepository tokenRepository;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity, JwtTokenValidator jwtTokenValidator) throws Exception {
        return httpSecurity
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth ->
                        auth.requestMatchers("/api/auth/**","/api/email/**", "/error","/api/password/**", "/api/token/**", "/api/suppliers/editPremium/**").permitAll()
                                .requestMatchers("/index.html", "/static/**", "/").permitAll()
                                .requestMatchers("/ws/**").permitAll()
                                .anyRequest().authenticated())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exceptions -> exceptions.accessDeniedHandler(customAccessDeniedHandler)
                        .authenticationEntryPoint(customAuthenticationEntryPoint))
                .addFilterBefore(jwtTokenValidator, UsernamePasswordAuthenticationFilter.class)
                .logout(logout -> {
                    logout.logoutUrl("/api/logout")
                            .addLogoutHandler((request,response,authentication) -> {
                                final var autheader = request.getHeader(HttpHeaders.AUTHORIZATION);
                                logout(autheader);
                            })
                            .logoutSuccessHandler((request,response,authentication) -> {
                                SecurityContextHolder.clearContext();
                            });
                })
                .build();
    }

    private void logout(final String token) {
        if(token == null || !token.startsWith("Bearer ")){
            throw new IllegalArgumentException("Invalid token");
        }

        final String jwtToken  = token.substring(7);
        final Token foundToken = tokenRepository.findByToken(jwtToken)
                .orElseThrow(() -> new IllegalArgumentException("Invalid token"));
        foundToken.setExpired(true);
        foundToken.setRevoked(true);
        tokenRepository.save(foundToken);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception{
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder());
        provider.setUserDetailsService(userDetailsService);
        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder () {
        return new BCryptPasswordEncoder();
    }

}

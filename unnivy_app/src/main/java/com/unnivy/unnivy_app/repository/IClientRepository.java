package com.unnivy.unnivy_app.repository;

import com.unnivy.unnivy_app.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IClientRepository extends JpaRepository<Client,Long> {

    Optional<Client> findClientEntityByUsername(String username);

    // Simplificado: Spring infiere automáticamente EXISTS(SELECT 1 FROM clients WHERE email = :email)
    boolean existsByEmail(String email);

    // CORREGIDO: Usamos la sintaxis correcta 'existsByUsername' para que JPA lo infiera.
    boolean existsByUsername(String username);

}

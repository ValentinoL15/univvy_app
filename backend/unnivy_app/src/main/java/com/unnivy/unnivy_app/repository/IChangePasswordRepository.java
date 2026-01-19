package com.unnivy.unnivy_app.repository;

import com.unnivy.unnivy_app.model.ChangePassword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IChangePasswordRepository extends JpaRepository<ChangePassword,Long> {

    Optional<ChangePassword> findByToken(String token);

    void deleteByEmail(String email);

}

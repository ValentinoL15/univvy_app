package com.unnivy.unnivy_app.repository;

import com.unnivy.unnivy_app.model.Token;
import com.unnivy.unnivy_app.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ITokenRepository extends JpaRepository<Token,Long> {

    @Query("SELECT t FROM Token t WHERE t.user.id = :userId AND (t.expired = false OR t.revoked = false)")
    List<Token> findAllValidTokensByUser(@Param("userId") Long userId);

    List<Token> findAllValidFalseOrRevokedIsFalseByUser(User user);

    @Query("SELECT t FROM Token t WHERE t.user.username = :username AND (t.expired = false OR t.revoked = false)")
    List<Token> findAllValidTokensByUsername(@Param("username") String username);

    Optional<Token> findByToken(String token);

}

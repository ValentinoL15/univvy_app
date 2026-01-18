package com.unnivy.unnivy_app.repository;

import com.unnivy.unnivy_app.model.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ISupplierRepository extends JpaRepository<Supplier,Long> {

    Optional<Supplier> findSupplierEntityByUsername(String username);

    boolean existsByEmail(String email);

}

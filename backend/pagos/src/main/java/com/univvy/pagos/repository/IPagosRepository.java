package com.univvy.pagos.repository;

import com.univvy.pagos.model.Pagos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IPagosRepository extends JpaRepository<Pagos,Long> {

    // Spring busca automáticamente por el atributo stripeCustomerId
    Optional<Pagos> findByStripeCustomerId(String stripeCustomerId);

    Optional<Pagos> findByPagoId(Long pagoId);

    Optional<Pagos> findByStripeSubscriptionId(String stripeSubscriptionId);

    boolean existsByUserId(Long userId);

    Optional<Pagos> findByUserId(Long userId);

}

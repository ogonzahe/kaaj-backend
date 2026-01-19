package com.kaaj.api.repository;

import com.kaaj.api.model.PagoStripe;
import com.kaaj.api.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;  // ??Agrega esta importaci??n!

public interface PagoStripeRepository extends JpaRepository<PagoStripe, Integer> {

    List<PagoStripe> findByUsuarioOrderByFechaCreacionDesc(Usuario usuario);

    List<PagoStripe> findByUsuarioAndEstadoOrderByFechaCreacionDesc(Usuario usuario, String estado);

    Optional<PagoStripe> findByStripePaymentId(String stripePaymentId);
}
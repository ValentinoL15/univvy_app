package com.univvy.pagos.service;

import com.stripe.model.checkout.Session;
import com.univvy.pagos.dto.PagosDTOs.CreatePagoDTO;
import com.univvy.pagos.dto.PagosDTOs.PagosDTO;
import com.univvy.pagos.dto.StripeResponse;

public interface IPagosService {

    public PagosDTO getPago(Long pago_id);

    public StripeResponse createPago(CreatePagoDTO pagoDTO, String currentUser);

    public void cancelSuscription(String suscriptionId);

    public void webhookEvent(String payload, String sigHeader);

}

package com.univvy.pagos.controller;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.univvy.pagos.dto.PagosDTOs.CreatePagoDTO;
import com.univvy.pagos.dto.StripeResponse;
import com.univvy.pagos.service.IPagosService;
import jakarta.ws.rs.core.Context;
import lombok.RequiredArgsConstructor;
import org.apache.http.client.methods.HttpHead;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/product/v1")
public class PagosController {

    private final IPagosService pagosService;

    @PostMapping("/checkout")
    public ResponseEntity<StripeResponse> createPago(@RequestBody CreatePagoDTO pagoDTO,
                                                     Principal principal) {
        StripeResponse stripeResponse = pagosService.createPago(pagoDTO, principal.getName());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(stripeResponse);
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> handleStripeWebhook(@RequestBody String payload,
                                                      @RequestHeader("Stripe-Signature") String sigHeader) {
        pagosService.webhookEvent(payload, sigHeader);
        return ResponseEntity.ok("Success");
    }

    @PostMapping("/cancelSub/{idSub}")
    public ResponseEntity<String> cancelSuscription(@PathVariable String idSub){
        pagosService.cancelSuscription(idSub);
        return ResponseEntity.ok("Suscripción cancelada con éxito");
    }

}

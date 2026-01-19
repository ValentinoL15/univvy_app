package com.univvy.pagos.service;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.stripe.Stripe;
import com.stripe.StripeClient;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.SubscriptionListParams;
import com.stripe.param.SubscriptionUpdateParams;
import com.stripe.param.checkout.SessionCreateParams;
import com.univvy.pagos.dto.PagosDTOs.CreatePagoDTO;
import com.univvy.pagos.dto.PagosDTOs.PagosDTO;
import com.univvy.pagos.dto.StripeResponse;
import com.univvy.pagos.dto.UserDTOs.UserDTO;
import com.univvy.pagos.exceptions.exceptionsHandler.PagoIdNotFoundException;
import com.univvy.pagos.exceptions.exceptionsHandler.WebhookDeserializationException;
import com.univvy.pagos.exceptions.exceptionsHandler.WebhookSignatureException;
import com.univvy.pagos.model.Pagos;
import com.univvy.pagos.model.PlanType;
import com.univvy.pagos.repository.IPagosRepository;
import com.univvy.pagos.repository.IUserApi;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;

import javax.swing.text.html.Option;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PagosService implements IPagosService{

    private final IPagosRepository pagosRepository;
    private final IUserApi userApi;

    private final ObjectMapper getJacksonObjectMapper;

    private final static String ID_MONTHLY = "price_1SltwyAmlBs3pwl2i1WxRiHh";
    private final static String ID_ANNUAL = "price_1SltwWAmlBs3pwl2OeTvghuY";

    @Value("${stripe.secret.key}")
    private String secretKey;

    @Value("${webhook.secret.key}")
    private String webhookKey;

    @Override
    public PagosDTO getPago(Long pago_id) {
        return null;
    }

    @Override
    @Transactional
    public StripeResponse createPago(CreatePagoDTO pagoDTO, String currentUser) {
        StripeClient client = new StripeClient(secretKey);

        UserDTO userDTO = userApi.getUser(pagoDTO.getUser_id());
        if (userDTO == null) throw new RuntimeException("El usuario no se encuentra");
        if (!currentUser.equals(userDTO.getUsername())) throw new RuntimeException("Sin permisos");

        System.out.println("User" + userDTO.getUsername());

        // 1️⃣ Buscar si ya existe el registro de pago o crear uno nuevo
        Pagos pago = pagosRepository.findByUserId(pagoDTO.getUser_id())
                .orElse(new Pagos());

        pago.setUserId(pagoDTO.getUser_id());
        pago.setStatus("PENDING");
        pago.setPlanType(pagoDTO.getProduct_id().equals(ID_MONTHLY) ? PlanType.MONTHLY : PlanType.ANNUAL);

        // Guardamos inicialmente para asegurar que tenemos el objeto persistido
        pago = pagosRepository.save(pago);

        try {
            String customerId;

            // 2️⃣ Lógica para NO duplicar el cliente en Stripe
            if (pago.getStripeCustomerId() != null && !pago.getStripeCustomerId().isEmpty()) {
                // Si ya tenemos el ID, lo usamos
                customerId = pago.getStripeCustomerId();
                System.out.println("Reutilizando cliente de Stripe: " + customerId);
            } else {
                // Si no existe en nuestra DB, lo creamos en Stripe
                Customer customer = client.v1().customers().create(
                        CustomerCreateParams.builder()
                                .setName(userDTO.getUsername())
                                .setEmail(userDTO.getEmail())
                                .putMetadata("userId", pago.getUserId().toString())
                                .build()
                );
                customerId = customer.getId();
                pago.setStripeCustomerId(customerId);
                System.out.println("Nuevo cliente creado en Stripe: " + customerId);
            }

            long unaHora = Instant.now()
                    .plus(1, ChronoUnit.HOURS)
                    .getEpochSecond();

            // 3️⃣ Crear sesión de Checkout usando el customerId (importante pasar .setCustomer())
            SessionCreateParams params = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                    .setCustomer(customerId) // <--- ESTO evita que Stripe pida datos de nuevo o cree otro cliente
                    .setSuccessUrl("http://localhost:8082/success")
                    .setCancelUrl("http://localhost:8082/cancel")
                    .setExpiresAt(unaHora)
                    .setClientReferenceId(pago.getUserId().toString())
                    .setSubscriptionData(
                            SessionCreateParams.SubscriptionData.builder()
                                    .putMetadata("pagoId", pago.getPagoId().toString())
                                    .build()
                    )
                    .addLineItem(SessionCreateParams.LineItem.builder()
                            .setPrice(pagoDTO.getProduct_id())
                            .setQuantity(1L)
                            .build())
                    .build();

            Session session = client.v1().checkout().sessions().create(params);

            // 4️⃣ Actualizar el registro final
            pago.setStripeSubscriptionId(session.getSubscription());
            pagosRepository.save(pago);

            return StripeResponse.builder()
                    .status("SUCCESS")
                    .sessionId(session.getId())
                    .sessionUrl(session.getUrl())
                    .build();

        } catch (StripeException e) {
            throw new RuntimeException("Error en Stripe: " + e.getMessage());
        }
    }

    @Override
    public void cancelSuscription(String suscriptionId) {
        try {
            Stripe.apiKey = secretKey;
            Subscription resource = Subscription.retrieve(suscriptionId);
            SubscriptionUpdateParams params = SubscriptionUpdateParams.builder()
                    .setCancelAtPeriodEnd(true)
                    .build();
            resource.update(params);
            Optional<Pagos> pagoSub = pagosRepository.findByStripeSubscriptionId(suscriptionId);
            Pagos pago = pagoSub.get();
            pago.setStatus("CANCELED_PENDING");
            pagosRepository.save(pago);

            UserDTO user = userApi.getUser(pago.getUserId());
            if(user.getUser_id() == null){
                throw new UsernameNotFoundException("Usuario no encontrado");
            }

            userApi.editSupplierPremium(pago.getStatus(), user.getUsername());

            System.out.println("Suscripción marcada para cancelar");
        } catch (StripeException e) {
            // Aquí manejas el error si Stripe dice que el ID no existe o no hay internet
            System.err.println("Error al conectar con Stripe: " + e.getMessage());
        }
    }


    @Override
    @Transactional
    public void webhookEvent(String payload, String sigHeader) {

        Event event = verifyEvent(payload,sigHeader);
        log.info("📩 Evento recibido: {}", event.getType());

        switch (event.getType()) {
            case "invoice.paid":
                handleInvoicePaid(event);
                break;
            case "invoice.payment_failed":
                handlePaymentFailed(event);
                break;
            case "customer.subscription.deleted":
                handleSubscriptionDeleted(event);
                break;
            default:
                log.warn("⚠️ Evento no manejado: {}", event.getType());
        }
    }

    private void handleInvoicePaid(Event event){
        Invoice invoice = deserializableInvoice(event);
        String pagoIdStr = extractPagoId(invoice);

        if (pagoIdStr == null) {
            log.error("❌ No se encontró pagoId en invoice {}", invoice.getId());
            throw new PagoIdNotFoundException("pagoId no encontrado en invoice: " + invoice.getId());
        }

        Long pagoId = Long.parseLong(pagoIdStr);
        log.debug("🔍 Procesando pago: {}", pagoId);

        Pagos pago = findPagoById(pagoId);

        // Idempotencia: evitar procesar pagos ya activos
        if (PagoStatus.ACTIVE.name().equals(pago.getStatus())) {
            log.info("⏭️ Pago {} ya estaba ACTIVO, omitiendo", pagoId);
            return;
        }

        updatePagoFromInvoice(pago, invoice);
        pago.setStatus(PagoStatus.ACTIVE.name());
        pagosRepository.save(pago);

        UserDTO user = userApi.getUser(pago.getUserId());
        if(user.getUser_id() == null){
            throw new UsernameNotFoundException("Usuario no encontrado");
        }

        userApi.editSupplierPremium(pago.getStatus(), user.getUsername());

        log.info("✅ Suscripción ACTIVADA para pagoId {}", pagoId);
    }

    private void handlePaymentFailed(Event event) {
        Invoice invoice = deserializableInvoice(event);
        String pagoIdStr = extractPagoId(invoice);

        if (pagoIdStr == null) {
            log.error("❌ No se encontró pagoId en invoice {}", invoice.getId());
            throw new PagoIdNotFoundException("pagoId no encontrado en invoice: " + invoice.getId());
        }

        Long pagoId = Long.parseLong(pagoIdStr);
        Pagos pago = findPagoById(pagoId);

        pago.setStatus(PagoStatus.FAILED.name());
        pagosRepository.save(pago);

        UserDTO user = userApi.getUser(pago.getUserId());
        if(user.getUser_id() == null){
            throw new UsernameNotFoundException("Usuario no encontrado");
        }

        userApi.editSupplierPremium(pago.getStatus(), user.getUsername());

        log.info("⚠️ Pago FALLIDO para pagoId {}", pagoId);
    }

    private void handleSubscriptionDeleted(Event event) {
        Subscription subscription = (Subscription) event.getDataObjectDeserializer()
                .getObject()
                .orElse(null);

        if (subscription == null) {
            log.error("❌ No se pudo deserializar la suscripción del evento");
            return;
        }

        log.debug("🔍 Procesando cancelación de suscripción: {}", subscription.getId());

        Optional<Pagos> pagoOptional = pagosRepository.findByStripeSubscriptionId(subscription.getId());

        pagoOptional.ifPresentOrElse(
                pago -> {
                    pago.setStatus(PagoStatus.CANCELED.name());

                    // Mantener la fecha real de fin de período (el usuario tiene acceso hasta entonces)
                    if (subscription.getEndedAt() != null) {
                        pago.setCurrentPeriodEnd(
                                Instant.ofEpochSecond(subscription.getEndedAt())
                                        .atZone(ZoneId.systemDefault())
                                        .toLocalDateTime()
                        );
                    }

                    pagosRepository.save(pago);

                    UserDTO user = userApi.getUser(pago.getUserId());
                    if(user.getUser_id() == null){
                        throw new UsernameNotFoundException("Usuario no encontrado");
                    }

                    userApi.editSupplierPremium(pago.getStatus(), user.getUsername());
                    log.info("✅ Suscripción CANCELADA en DB: {}", subscription.getId());
                },
                () -> log.warn("⚠️ No se encontró pago para la suscripción: {}", subscription.getId())
        );
    }

    private Event verifyEvent(String payload, String sigHeader) {
        try {
            return Webhook.constructEvent(payload, sigHeader, webhookKey);
        } catch (SignatureVerificationException e) {
            log.error("❌ Error de verificación de firma del webhook", e);
            throw new WebhookSignatureException("Firma del webhook inválida", e);
        }
    }

    private void updatePagoFromInvoice(Pagos pago, Invoice invoice) {
        // Customer ID
        if (invoice.getCustomer() != null) {
            pago.setStripeCustomerId(invoice.getCustomer());
        }

        // Subscription ID
        if (invoice.getParent() != null
                && invoice.getParent().getSubscriptionDetails() != null
                && invoice.getParent().getSubscriptionDetails().getSubscription() != null) {

            pago.setStripeSubscriptionId(
                    invoice.getParent().getSubscriptionDetails().getSubscription()
            );
        }

        // Períodos de facturación
        if (invoice.getLines() != null
                && invoice.getLines().getData() != null
                && !invoice.getLines().getData().isEmpty()) {

            var period = invoice.getLines().getData().get(0).getPeriod();
            if (period != null) {
                if (period.getStart() != null) {
                    pago.setCurrentPeriodStart(
                            Instant.ofEpochSecond(period.getStart())
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDateTime()
                    );
                }
                if (period.getEnd() != null) {
                    pago.setCurrentPeriodEnd(
                            Instant.ofEpochSecond(period.getEnd())
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDateTime()
                    );
                }
            }
        }
    }

    private Invoice deserializableInvoice(Event event) {
        return (Invoice) event.getDataObjectDeserializer().getObject()
                .orElseThrow(() -> new WebhookDeserializationException("No se pudo deserializar Invoice"));
    }

    private String extractPagoId(Invoice invoice) {
        String pagoIdStr = null;

        // Intentar obtener de subscription metadata
        if (invoice.getParent() != null
                && invoice.getParent().getSubscriptionDetails() != null
                && invoice.getParent().getSubscriptionDetails().getMetadata() != null) {

            pagoIdStr = invoice.getParent()
                    .getSubscriptionDetails()
                    .getMetadata()
                    .get("pagoId");
        }

        // Fallback: obtener de line items metadata
        if (pagoIdStr == null
                && invoice.getLines() != null
                && invoice.getLines().getData() != null
                && !invoice.getLines().getData().isEmpty()) {

            var lineItem = invoice.getLines().getData().get(0);
            if (lineItem.getMetadata() != null) {
                pagoIdStr = lineItem.getMetadata().get("pagoId");
            }
        }

        return pagoIdStr;
    }

    private Pagos findPagoById(Long pagoId) {
        return pagosRepository.findByPagoId(pagoId)
                .orElseThrow(() -> new PagoIdNotFoundException("Pago no encontrado: " + pagoId));
    }

    public enum PagoStatus {
        PENDING,
        ACTIVE,
        FAILED,
        CANCELED
    }


}

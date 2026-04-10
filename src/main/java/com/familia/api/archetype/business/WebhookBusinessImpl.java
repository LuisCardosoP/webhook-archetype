package com.familia.api.archetype.business;

import com.familia.api.archetype.model.PaymentEvent;
import com.familia.api.archetype.model.Response;
import com.google.gson.Gson;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Service
public class WebhookBusinessImpl implements WebhookBusiness {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebhookBusinessImpl.class);
    private static final String HMAC_ALGO = "HmacSHA256";

    @Value("${twilio.account-sid}")
    private String twilioAccountSid;

    @Value("${twilio.auth-token}")
    private String twilioAuthToken;

    @Value("${twilio.from-number}")
    private String twilioFromNumber;

    @Value("${webhook.secret}")
    private String webhookSecret;

    @Value("${PORT:${server.port:8239}}")
    private String serverPort;

    @Value("${server.servlet.context-path:/webhook-sms}")
    private String contextPath;

    private final RestTemplate restTemplate = new RestTemplate();
    private final Gson gson = new Gson();

    @Override
    public Response<String> simulatePurchase(PaymentEvent event) {
        LOGGER.info("Compra iniciada para orden: {}", event.getOrderId());

        event.setStatus("SUCCESS");

        String payload = gson.toJson(event);
        String signature = generateHmacSignature(payload, webhookSecret);

        String webhookUrl = "http://localhost:" + serverPort + contextPath + "/api/v1/webhook/payment";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Webhook-Signature", signature);

        HttpEntity<String> request = new HttpEntity<>(payload, headers);

        LOGGER.info("Disparando webhook a: {}", webhookUrl);
        restTemplate.postForEntity(webhookUrl, request, String.class);

        return new Response<>(HttpStatus.OK.value(), "Compra procesada, webhook disparado", "OK", null, null);
    }

    @Override
    public Response<String> processPaymentWebhook(String signature, PaymentEvent event) {
        LOGGER.info("Webhook recibido para orden: {}", event.getOrderId());

        String payload = gson.toJson(event);
        String expectedSignature = generateHmacSignature(payload, webhookSecret);

        if (!expectedSignature.equals(signature)) {
            LOGGER.warn("Firma inválida para orden: {}", event.getOrderId());
            return new Response<>(HttpStatus.UNAUTHORIZED.value(), "Firma inválida", "UNAUTHORIZED", null, null);
        }

        if ("SUCCESS".equals(event.getStatus())) {
            sendSms(event);
        } else {
            LOGGER.info("Pago fallido para orden: {}, no se envía SMS", event.getOrderId());
        }

        return new Response<>(HttpStatus.OK.value(), "Webhook procesado correctamente", "OK", null, null);
    }

    private void sendSms(PaymentEvent event) {
        LOGGER.info("Enviando SMS a: {}", event.getPhoneNumber());

        Twilio.init(twilioAccountSid, twilioAuthToken);

        String smsBody = String.format(
            "Hola %s! Tu compra de %s por %.2f %s fue exitosa. Orden: %s",
            event.getCustomerName(),
            event.getProductName(),
            event.getAmount(),
            event.getCurrency(),
            event.getOrderId()
        );

        Message message = Message.creator(
            new PhoneNumber(event.getPhoneNumber()),
            new PhoneNumber(twilioFromNumber),
            smsBody
        ).create();

        LOGGER.info("SMS enviado. SID: {}", message.getSid());
    }

    private String generateHmacSignature(String payload, String secret) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGO);
            SecretKeySpec keySpec = new SecretKeySpec(secret.getBytes(), HMAC_ALGO);
            mac.init(keySpec);
            byte[] hash = mac.doFinal(payload.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            LOGGER.error("Error generando firma HMAC", e);
            throw new RuntimeException("Error generando firma", e);
        }
    }
}

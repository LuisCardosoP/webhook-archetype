package com.familia.api.archetype.rest;

import com.familia.api.archetype.business.WebhookBusiness;
import com.familia.api.archetype.model.PaymentEvent;
import com.familia.api.archetype.model.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/")
@Tag(name = "webhook", description = "Receptor de webhooks de pago")
public class WebhookRest {

    private final WebhookBusiness webhookBusiness;

    public WebhookRest(WebhookBusiness webhookBusiness) {
        this.webhookBusiness = webhookBusiness;
    }

    @Operation(description = "Recibe el webhook de pago, valida la firma y envía SMS de confirmación")
    @PostMapping(value = "webhook/payment", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<String> receivePayment(
            @RequestHeader(value = "X-Webhook-Signature") String signature,
            @RequestBody PaymentEvent event
    ) {
        return this.webhookBusiness.processPaymentWebhook(signature, event);
    }
}

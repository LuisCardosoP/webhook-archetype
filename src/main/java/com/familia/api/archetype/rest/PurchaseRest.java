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
@Tag(name = "purchase", description = "Simula una compra y dispara el webhook")
public class PurchaseRest {

    private final WebhookBusiness webhookBusiness;

    public PurchaseRest(WebhookBusiness webhookBusiness) {
        this.webhookBusiness = webhookBusiness;
    }

    @Operation(description = "Simula una compra y dispara el webhook de pago exitoso")
    @PostMapping(value = "purchase", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<String> purchase(@RequestBody PaymentEvent event) {
        return this.webhookBusiness.simulatePurchase(event);
    }
}

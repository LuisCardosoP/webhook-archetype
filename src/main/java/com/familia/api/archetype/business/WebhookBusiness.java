package com.familia.api.archetype.business;

import com.familia.api.archetype.model.PaymentEvent;
import com.familia.api.archetype.model.Response;

public interface WebhookBusiness {

    Response<String> simulatePurchase(PaymentEvent event);

    Response<String> processPaymentWebhook(String signature, PaymentEvent event);
}

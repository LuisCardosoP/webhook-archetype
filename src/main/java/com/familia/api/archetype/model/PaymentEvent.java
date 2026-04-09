package com.familia.api.archetype.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Payload del evento de pago disparado por el webhook
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentEvent {

    private String orderId;
    private String customerName;
    private String phoneNumber;   // formato E.164: +521234567890
    private String productName;
    private double amount;
    private String currency;
    private String status;        // SUCCESS, FAILED
}

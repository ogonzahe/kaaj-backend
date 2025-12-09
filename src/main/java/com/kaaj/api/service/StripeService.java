package com.kaaj.api.service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
public class StripeService {

    @Value("${stripe.api.secret-key}")
    private String secretKey;

    public String crearPaymentIntent(BigDecimal monto, String descripcion, String correo) throws StripeException {
        Stripe.apiKey = secretKey;

        long amountInCents = monto.multiply(new BigDecimal("100")).longValue();

        Map<String, Object> params = new HashMap<>();
        params.put("amount", amountInCents);
        params.put("currency", "mxn");
        params.put("description", descripcion);

        Map<String, String> metadata = new HashMap<>();
        metadata.put("email", correo);
        params.put("metadata", metadata);

        PaymentIntent intent = PaymentIntent.create(params);
        return intent.getClientSecret();
    }
}
package com.kaaj.api.service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Customer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
public class StripeService {

    @Value("${stripe.api.secret-key}")
    private String secretKey;

    public String crearCustomerParaUsuario(String email, String nombre) throws StripeException {
        Stripe.apiKey = secretKey;

        Map<String, Object> params = new HashMap<>();
        params.put("email", email);
        params.put("name", nombre);
        params.put("description", "Cliente del condominio");

        Customer customer = Customer.create(params);
        return customer.getId();
    }

    public String crearPaymentIntent(BigDecimal monto, String descripcion, String correo) throws StripeException {
        return crearPaymentIntentParaUsuario(monto, descripcion, correo, null, null);
    }

    public String crearPaymentIntentParaUsuario(BigDecimal monto, String descripcion, String correo, String nombre, String stripeCustomerId) throws StripeException {
        Stripe.apiKey = secretKey;

        long amountInCents = monto.multiply(new BigDecimal("100")).longValue();

        Map<String, Object> params = new HashMap<>();
        params.put("amount", amountInCents);
        params.put("currency", "mxn");
        params.put("description", descripcion);

        if (stripeCustomerId != null && !stripeCustomerId.isEmpty()) {
            params.put("customer", stripeCustomerId);
        }

        Map<String, String> metadata = new HashMap<>();
        metadata.put("email", correo);
        if (nombre != null) {
            metadata.put("usuario_nombre", nombre);
        }
        if (stripeCustomerId != null) {
            metadata.put("stripe_customer_id", stripeCustomerId);
        }
        params.put("metadata", metadata);

        PaymentIntent intent = PaymentIntent.create(params);
        return intent.getClientSecret();
    }
}
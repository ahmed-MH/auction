package com.jdk.encher.service;

import com.jdk.encher.dto.UserCreditDTO;
import com.jdk.encher.entity.Utilisateur;
import com.jdk.encher.repository.UtilisateurRepository;
import com.stripe.Stripe;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final UtilisateurRepository utilisateurRepository;

    @Value("${stripe.apiKey}")
    private String stripeApiKey;

    @Value("${stripe.currency:eur}")
    private String currency;

    private final int PRICE_PER_CREDIT_CENTS = 100;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeApiKey;
    }

    public Map<String, Object> createPaymentIntent(Utilisateur user, int credits) {
        long amountCents = (long) credits * PRICE_PER_CREDIT_CENTS;

        if (amountCents < 50) { // minimum 0.50 EUR
            throw new RuntimeException("Le montant total doit être au moins 0.50 EUR");
        }

        try {
            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(amountCents)
                    .setCurrency(currency)
                    .addPaymentMethodType("card")
                    .putMetadata("userId", String.valueOf(user.getId()))
                    .putMetadata("credits", String.valueOf(credits))
                    .build();

            PaymentIntent intent = PaymentIntent.create(params);

            Map<String, Object> response = new HashMap<>();
            response.put("clientSecret", intent.getClientSecret());
            response.put("paymentIntentId", intent.getId());
            response.put("amount", amountCents);

            return response;
        } catch (Exception e) {
            throw new RuntimeException("Erreur Stripe: " + e.getMessage(), e);
        }
    }

    public UserCreditDTO creditAfterPayment(Long userId, String paymentIntentId) {
        try {
            PaymentIntent intent = PaymentIntent.retrieve(paymentIntentId);
            if (!"succeeded".equals(intent.getStatus())) {
                throw new RuntimeException("PaymentIntent status not succeeded: " + intent.getStatus());
            }

            String creditsMeta = intent.getMetadata().get("credits");
            int credits = Integer.parseInt(creditsMeta);

            Utilisateur u = utilisateurRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

            u.setSoldeCredit(u.getSoldeCredit() + credits);
            utilisateurRepository.save(u);

            return new UserCreditDTO(u.getId(), u.getNom(), u.getEmail(), u.getSoldeCredit());

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors du traitement du paiement: " + e.getMessage(), e);
        }
    }
}

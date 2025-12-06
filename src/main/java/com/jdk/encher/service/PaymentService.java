package com.jdk.encher.service;

import com.jdk.encher.entity.StatutPaiement;
import com.jdk.encher.entity.TransactionCredit;
import com.jdk.encher.entity.Utilisateur;
import com.jdk.encher.repository.TransactionCreditRepository;
import com.jdk.encher.repository.UtilisateurRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class PaymentService {

    @Value("${stripe.apiKey}")
    private String stripeApiKey;

    @Value("${stripe.currency}")
    private String currency;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private TransactionCreditRepository transactionCreditRepository;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeApiKey;
    }

    /**
     * Crée un PaymentIntent Stripe pour un montant donné
     */
    public Map<String, String> createPaymentIntent(Long userId, int montant) throws StripeException {
        // Vérifier que l'utilisateur existe
        Optional<Utilisateur> userOpt = utilisateurRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("Utilisateur non trouvé");
        }

        // Convertir le montant en centimes (Stripe utilise les centimes)
        long amountInCents = montant * 100L; // 1 crédit = 1 USD = 100 centimes

        // Créer le PaymentIntent
        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(amountInCents)
                .setCurrency(currency.toLowerCase())
                .setAutomaticPaymentMethods(
                        PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                .setEnabled(true)
                                .build()
                )
                .putMetadata("userId", userId.toString())
                .putMetadata("credits", String.valueOf(montant))
                .build();

        PaymentIntent paymentIntent = PaymentIntent.create(params);

        // Enregistrer la transaction en attente
        TransactionCredit transaction = new TransactionCredit();
        transaction.setDateTransaction(LocalDateTime.now());
        transaction.setMontant(montant);
        transaction.setNbCredits(montant);
        transaction.setModePaiement("Stripe");
        transaction.setPaymentIntentId(paymentIntent.getId());
        transaction.setStatut(StatutPaiement.EN_ATTENTE);
        transaction.setUtilisateur(userOpt.get());
        transactionCreditRepository.save(transaction);

        Map<String, String> response = new HashMap<>();
        response.put("clientSecret", paymentIntent.getClientSecret());
        response.put("paymentIntentId", paymentIntent.getId());
        return response;
    }

    /**
     * Confirme le paiement et crédite l'utilisateur
     */
    public Utilisateur confirmPayment(Long userId, String paymentIntentId) throws StripeException {
        // Récupérer le PaymentIntent depuis Stripe
        PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);

        // Vérifier que le paiement a réussi
        if (!"succeeded".equals(paymentIntent.getStatus())) {
            throw new RuntimeException("Le paiement n'a pas réussi. Statut: " + paymentIntent.getStatus());
        }

        // Récupérer l'utilisateur
        Optional<Utilisateur> userOpt = utilisateurRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("Utilisateur non trouvé");
        }

        Utilisateur user = userOpt.get();

        // Récupérer le nombre de crédits depuis les métadonnées
        int credits = Integer.parseInt(paymentIntent.getMetadata().getOrDefault("credits", "0"));

        // Créditer l'utilisateur
        user.setSoldeCredit(user.getSoldeCredit() + credits);
        utilisateurRepository.save(user);

        // Mettre à jour la transaction
        Optional<TransactionCredit> transactionOpt = transactionCreditRepository.findByPaymentIntentId(paymentIntentId);

        if (transactionOpt.isPresent()) {
            TransactionCredit transaction = transactionOpt.get();
            transaction.setStatut(StatutPaiement.SUCCES);
            transactionCreditRepository.save(transaction);
        } else {
            // Créer une nouvelle transaction si on ne trouve pas l'ancienne
            TransactionCredit transaction = new TransactionCredit();
            transaction.setDateTransaction(LocalDateTime.now());
            transaction.setMontant(credits);
            transaction.setNbCredits(credits);
            transaction.setModePaiement("Stripe");
            transaction.setPaymentIntentId(paymentIntentId);
            transaction.setStatut(StatutPaiement.SUCCES);
            transaction.setUtilisateur(user);
            transactionCreditRepository.save(transaction);
        }

        return user;
    }
}


package com.jdk.encher.controller;

import com.jdk.encher.config.JwtUtil;
import com.jdk.encher.entity.Utilisateur;
import com.jdk.encher.repository.UtilisateurRepository;
import com.jdk.encher.service.PaymentService;
import com.stripe.exception.StripeException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/payment")
@CrossOrigin(origins = "*")
@Tag(name = "Payment", description = "Gestion des paiements Stripe")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Operation(summary = "Créer un PaymentIntent Stripe")
    @PostMapping("/create-intent")
    public ResponseEntity<?> createPaymentIntent(
            @RequestBody Map<String, Object> request,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            // Extraire le token JWT
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(401).body(Map.of("error", "Token manquant"));
            }

            String token = authHeader.substring(7);
            String email = jwtUtil.extractUsername(token);

            // Vérifier le token et récupérer l'utilisateur
            if (!jwtUtil.validateToken(token, email)) {
                return ResponseEntity.status(401).body(Map.of("error", "Token invalide"));
            }

            Optional<Utilisateur> userOpt = utilisateurRepository.findByEmail(email);
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(404).body(Map.of("error", "Utilisateur non trouvé"));
            }

            Utilisateur user = userOpt.get();
            Integer montant = (Integer) request.get("montant");

            if (montant == null || montant <= 0) {
                return ResponseEntity.badRequest().body(Map.of("error", "Montant invalide"));
            }

            // Créer le PaymentIntent
            Map<String, String> result = paymentService.createPaymentIntent(user.getId(), montant);

            return ResponseEntity.ok(result);
        } catch (StripeException e) {
            return ResponseEntity.status(500).body(Map.of("error", "Erreur Stripe: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Erreur serveur: " + e.getMessage()));
        }
    }

    @Operation(summary = "Confirmer un paiement et créditer l'utilisateur")
    @PostMapping("/confirm")
    public ResponseEntity<?> confirmPayment(
            @RequestBody Map<String, String> request,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            // Extraire le token JWT
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(401).body(Map.of("error", "Token manquant"));
            }

            String token = authHeader.substring(7);
            String email = jwtUtil.extractUsername(token);

            // Vérifier le token et récupérer l'utilisateur
            if (!jwtUtil.validateToken(token, email)) {
                return ResponseEntity.status(401).body(Map.of("error", "Token invalide"));
            }

            Optional<Utilisateur> userOpt = utilisateurRepository.findByEmail(email);
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(404).body(Map.of("error", "Utilisateur non trouvé"));
            }

            Utilisateur user = userOpt.get();
            String paymentIntentId = request.get("paymentIntentId");

            if (paymentIntentId == null || paymentIntentId.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "paymentIntentId manquant"));
            }

            // Confirmer le paiement
            Utilisateur updatedUser = paymentService.confirmPayment(user.getId(), paymentIntentId);

            // Retourner les informations de l'utilisateur mis à jour
            Map<String, Object> response = new HashMap<>();
            response.put("id", updatedUser.getId());
            response.put("email", updatedUser.getEmail());
            response.put("nom", updatedUser.getNom());
            response.put("role", updatedUser.getRole().name());
            response.put("soldeCredit", updatedUser.getSoldeCredit());

            return ResponseEntity.ok(response);
        } catch (StripeException e) {
            return ResponseEntity.status(500).body(Map.of("error", "Erreur Stripe: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Erreur serveur: " + e.getMessage()));
        }
    }
}


package com.jdk.encher.controller;

import com.jdk.encher.dto.PaymentRequestDTO;
import com.jdk.encher.dto.UserCreditDTO;
import com.jdk.encher.entity.Utilisateur;
import com.jdk.encher.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/create-intent")
    public ResponseEntity<?> createIntent(
            @AuthenticationPrincipal Utilisateur currentUser,
            @RequestBody PaymentRequestDTO dto) {

        Map<String, Object> intentData = paymentService.createPaymentIntent(currentUser, dto.getMontant());
        return ResponseEntity.ok(intentData);
    }

    @PostMapping("/confirm")
    public ResponseEntity<?> confirmAndCredit(
            @AuthenticationPrincipal Utilisateur currentUser,
            @RequestBody Map<String, String> body) {

        String paymentIntentId = body.get("paymentIntentId");
        if (paymentIntentId == null) return ResponseEntity.badRequest().body("paymentIntentId manquant");

        UserCreditDTO updated = paymentService.creditAfterPayment(currentUser.getId(), paymentIntentId);
        return ResponseEntity.ok(updated);
    }
}

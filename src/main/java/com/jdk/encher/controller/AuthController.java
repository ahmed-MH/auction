package com.jdk.encher.controller;

import com.jdk.encher.config.JwtUtil;
import com.jdk.encher.dto.JwtResponse;
import com.jdk.encher.dto.LoginRequest;
import com.jdk.encher.dto.SignUpRequest;
import com.jdk.encher.entity.Role;
import com.jdk.encher.entity.Utilisateur;
import com.jdk.encher.repository.UtilisateurRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "Authentication", description = "Gestion de l'authentification")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;
    private final com.jdk.encher.service.EmailService emailService;
    private final JwtUtil jwtUtil;

    @Operation(summary = "Connexion d'un utilisateur")
    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        Utilisateur utilisateur = utilisateurRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // Vérification si le compte est actif
        if (!utilisateur.isEtatCompte()) {
            return ResponseEntity.status(403).body(Map.of("message", "Compte non vérifié. Veuillez vérifier votre email."));
        }

        String jwt = jwtUtil.generateToken(utilisateur.getEmail(), utilisateur.getRole().name());

        return ResponseEntity.ok(new JwtResponse(
                jwt,
                "Bearer",
                utilisateur.getId(),
                utilisateur.getEmail(),
                utilisateur.getNom(),
                utilisateur.getRole().name(),
                utilisateur.getSoldeCredit()));
    }

    @Operation(summary = "Inscription d'un utilisateur")
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {

        if (utilisateurRepository.existsByEmail(signUpRequest.getEmail())) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Erreur : cet email est déjà utilisé !");
            return ResponseEntity.badRequest().body(errorResponse);
        }

        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setNom(signUpRequest.getNom());
        utilisateur.setEmail(signUpRequest.getEmail());
        utilisateur.setMotDePasse(passwordEncoder.encode(signUpRequest.getPassword()));
        
        // Configuration pour la vérification
        utilisateur.setEtatCompte(false); // Compte inactif par défaut
        String verificationCode = String.format("%06d", new java.util.Random().nextInt(999999));
        utilisateur.setVerificationCode(verificationCode);
        utilisateur.setVerificationCodeExpiresAt(java.time.LocalDateTime.now().plusMinutes(15));
        
        utilisateur.setSoldeCredit(0);

        if (signUpRequest.getRoles() != null && !signUpRequest.getRoles().isEmpty()) {
            String roleStr = signUpRequest.getRoles().iterator().next().toUpperCase();
            utilisateur.setRole(Role.valueOf(roleStr));
        } else {
            utilisateur.setRole(Role.USER);
        }

        utilisateurRepository.save(utilisateur);

        // Envoyer l'email
        try {
            emailService.sendVerificationEmail(utilisateur.getEmail(), verificationCode);
        } catch (Exception e) {
            // RETOURNER L'ERREUR AU FRONTEND pour le debug
            System.out.println("ERREUR EMAIL: " + e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Erreur envoi email: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }

        Map<String, Object> successResponse = new HashMap<>();
        successResponse.put("message", "Inscription réussie ! Veuillez vérifier votre email pour activer votre compte.");
        
        return ResponseEntity.ok(successResponse);
    }

    @Operation(summary = "Vérification du compte")
    @PostMapping("/verify")
    public ResponseEntity<?> verifyAccount(@Valid @RequestBody com.jdk.encher.dto.VerifyRequest verifyRequest) {
        Utilisateur utilisateur = utilisateurRepository.findByEmail(verifyRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        if (utilisateur.isEtatCompte()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Le compte est déjà activé."));
        }

        if (utilisateur.getVerificationCode() == null || !utilisateur.getVerificationCode().equals(verifyRequest.getCode())) {
            return ResponseEntity.badRequest().body(Map.of("message", "Code de vérification invalide."));
        }

        if (utilisateur.getVerificationCodeExpiresAt().isBefore(java.time.LocalDateTime.now())) {
            return ResponseEntity.badRequest().body(Map.of("message", "Le code de vérification a expiré."));
        }

        utilisateur.setEtatCompte(true);
        utilisateur.setVerificationCode(null);
        utilisateur.setVerificationCodeExpiresAt(null);
        utilisateurRepository.save(utilisateur);

        return ResponseEntity.ok(Map.of("message", "Compte vérifié avec succès ! Vous pouvez maintenant vous connecter."));
    }
}

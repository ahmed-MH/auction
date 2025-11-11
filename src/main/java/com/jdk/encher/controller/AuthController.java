package com.jdk.encher.controller;

import com.jdk.encher.config.JwtUtil;
import com.jdk.encher.dto.JwtResponse;
import com.jdk.encher.dto.LoginRequest;
import com.jdk.encher.dto.SignUpRequest;
import com.jdk.encher.entity.Role;
import com.jdk.encher.entity.Utilisateur;
import com.jdk.encher.repository.UtilisateurRepository;
import com.jdk.encher.service.CustomUserDetailsService;
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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Tag(name = "Authentication", description = "Gestion de l'authentification")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Operation(summary = "Connexion d'un utilisateur")
    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        Utilisateur utilisateur = utilisateurRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        String jwt = jwtUtil.generateToken(utilisateur.getEmail());

        return ResponseEntity.ok(new JwtResponse(
                jwt,
                "Bearer",
                utilisateur.getId(),
                utilisateur.getEmail(),
                utilisateur.getNom(),
                utilisateur.getRole().name()
        ));
    }

    @Operation(summary = "Inscription d'un utilisateur")
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {

        if (utilisateurRepository.existsByEmail(signUpRequest.getEmail())) {
            // On renvoie un JSON même pour l'erreur
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Erreur : cet email est déjà utilisé !");
            return ResponseEntity.badRequest().body(errorResponse);
        }

        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setNom(signUpRequest.getNom());
        utilisateur.setEmail(signUpRequest.getEmail());
        utilisateur.setMotDePasse(passwordEncoder.encode(signUpRequest.getPassword()));
        utilisateur.setEtatCompte(true);

        // Mapper les rôles reçus
        if (signUpRequest.getRoles() != null && !signUpRequest.getRoles().isEmpty()) {
            String roleStr = signUpRequest.getRoles().iterator().next().toUpperCase();
            utilisateur.setRole(Role.valueOf(roleStr));
        } else {
            utilisateur.setRole(Role.USER);
        }

        Utilisateur savedUser = utilisateurRepository.save(utilisateur);

        // Réponse JSON pour le frontend
        Map<String, Object> successResponse = new HashMap<>();
        successResponse.put("message", "Inscription réussie !");
        successResponse.put("user", Map.of(
                "id", savedUser.getId(),
                "nom", savedUser.getNom(),
                "email", savedUser.getEmail(),
                "role", savedUser.getRole().name()
        ));

        return ResponseEntity.ok(successResponse);
    }

}

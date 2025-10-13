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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Set;
@Tag(name = "Authentication", description = "Gestion de l'authentification")
@RestController
@CrossOrigin
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Operation(summary = "Connexion d'un utilisateur")
    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtil.generateToken(loginRequest.getEmail());

        Utilisateur utilisateur = utilisateurRepository.findByEmail(loginRequest.getEmail()).orElse(null);

        assert utilisateur != null;
        return ResponseEntity.ok(new JwtResponse(
                jwt,
                utilisateur.getId(),
                utilisateur.getEmail(),
                utilisateur.getNom(),
                utilisateur.getRole().name()
        ));
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody SignUpRequest signUpRequest) {
        if (utilisateurRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest().body("Error: Email is already in use!");
        }

        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setNom(signUpRequest.getNom());
        utilisateur.setEmail(signUpRequest.getEmail());
        utilisateur.setMotDePasse(passwordEncoder.encode(signUpRequest.getPassword()));
        utilisateur.setRole(Role.USER); // Par défaut
        utilisateur.setEtatCompte(true);

        utilisateurRepository.save(utilisateur);

        return ResponseEntity.ok("User registered successfully!");
    }
}
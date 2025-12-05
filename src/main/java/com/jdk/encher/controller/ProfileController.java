package com.jdk.encher.controller;

import com.jdk.encher.dto.UpdatePasswordDTO;
import com.jdk.encher.dto.UpdateProfileDTO;
import com.jdk.encher.entity.Utilisateur;
import com.jdk.encher.repository.EnchereRepository;
import com.jdk.encher.repository.ParticipationRepository;
import com.jdk.encher.service.UtilisateurService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final UtilisateurService utilisateurService;
    private final EnchereRepository enchereRepository;
    private final ParticipationRepository participationRepository;

    // Modifier nom et email
    @PutMapping("/update")
    public ResponseEntity<?> updateProfile(
            @AuthenticationPrincipal Utilisateur currentUser,
            @RequestBody UpdateProfileDTO dto) {

        Utilisateur updated = utilisateurService.updateProfile(currentUser.getId(), dto);
        return ResponseEntity.ok(updated);
    }

    // Modifier mot de passe
    @PutMapping("/password")
    public ResponseEntity<?> updatePassword(
            @AuthenticationPrincipal Utilisateur currentUser,
            @RequestBody UpdatePasswordDTO dto) {

        utilisateurService.updatePassword(currentUser.getId(), dto);
        return ResponseEntity.ok("Mot de passe modifié avec succès");
    }

    // Supprimer le compte
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteAccount(@AuthenticationPrincipal Utilisateur currentUser) {

        // Vérifier si l’utilisateur a créé des enchères
        boolean hasCreatedAuctions = enchereRepository.existsByCreateurId(currentUser.getId());

        // Vérifier s’il a participé à des enchères
        boolean hasParticipations = participationRepository.existsByUtilisateurId(currentUser.getId());

        if (hasCreatedAuctions || hasParticipations) {
            return ResponseEntity
                    .status(403)
                    .body("❌ Vous ne pouvez pas supprimer votre compte car vous avez créé ou participé à une enchère.");
        }

        utilisateurService.deleteAccount(currentUser.getId());
        utilisateurService.deleteAccount(currentUser.getId());
        return ResponseEntity.ok("Compte supprimé définitivement");
    }

    // Récupérer les statistiques du profil (crédits bloqués, etc.)
    @GetMapping("/stats")
    public ResponseEntity<?> getProfileStats(@AuthenticationPrincipal Utilisateur currentUser) {
        Double montantBloque = participationRepository.sumMontantBloqueByUtilisateurId(currentUser.getId()).orElse(0.0);
        return ResponseEntity.ok(java.util.Map.of(
                "soldeCredit", currentUser.getSoldeCredit(),
                "montantBloque", montantBloque));
    }

}

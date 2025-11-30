package com.jdk.encher.controller;

import com.jdk.encher.dto.UpdatePasswordDTO;
import com.jdk.encher.dto.UpdateProfileDTO;
import com.jdk.encher.entity.Utilisateur;
import com.jdk.encher.repository.EncherRepository;
import com.jdk.encher.repository.ParticipationRepository;
import com.jdk.encher.service.UtilisateurService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final UtilisateurService utilisateurService;
    private final EncherRepository encherRepository;
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
        boolean hasCreatedAuctions = encherRepository.existsByCreateurId(currentUser.getId());

        // Vérifier s’il a participé à des enchères
        boolean hasParticipations = participationRepository.existsByUtilisateurId(currentUser.getId());

        if (hasCreatedAuctions || hasParticipations) {
            return ResponseEntity
                    .status(403)
                    .body("❌ Vous ne pouvez pas supprimer votre compte car vous avez créé ou participé à une enchère.");
        }

        utilisateurService.deleteAccount(currentUser.getId());
        return ResponseEntity.ok("Compte supprimé définitivement");
    }

}

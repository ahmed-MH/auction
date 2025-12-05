package com.jdk.encher.controller;

import com.jdk.encher.dto.ParticipationDTO;
import com.jdk.encher.service.ParticipationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/participations")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ParticipationController {

    private final ParticipationService participationService;

    /**
     * Ajouter ou mettre à jour une participation à une enchère
     * POST
     * /api/participations/enchere/{encherId}/utilisateur/{utilisateurId}?montant=XXX
     */
    @PostMapping("/enchere/{enchereId}/utilisateur/{utilisateurId}")
    public ResponseEntity<?> ajouterParticipation(
            @PathVariable Long enchereId,
            @PathVariable Long utilisateurId,
            @RequestParam Double montant) {
        try {
            ParticipationDTO participation = participationService.ajouterParticipation(enchereId, utilisateurId,
                    montant);
            return ResponseEntity.status(HttpStatus.CREATED).body(participation);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Obtenir toutes les participations d'une enchère
     * GET /api/participations/enchere/{encherId}
     */
    @GetMapping("/enchere/{enchereId}")
    public ResponseEntity<List<ParticipationDTO>> getParticipationsByEnchere(@PathVariable Long enchereId) {
        List<ParticipationDTO> participations = participationService.getParticipationsByEnchere(enchereId);
        return ResponseEntity.ok(participations);
    }

    /**
     * Obtenir toutes les participations d'un utilisateur
     * GET /api/participations/utilisateur/{utilisateurId}
     */
    @GetMapping("/utilisateur/{utilisateurId}")
    public ResponseEntity<List<ParticipationDTO>> getParticipationsByUtilisateur(@PathVariable Long utilisateurId) {
        List<ParticipationDTO> participations = participationService.getParticipationsByUtilisateur(utilisateurId);
        return ResponseEntity.ok(participations);
    }

    /**
     * Obtenir une participation spécifique
     * GET /api/participations/enchere/{encherId}/utilisateur/{utilisateurId}
     */
    @GetMapping("/enchere/{enchereId}/utilisateur/{utilisateurId}")
    public ResponseEntity<?> getParticipation(
            @PathVariable Long enchereId,
            @PathVariable Long utilisateurId) {
        try {
            ParticipationDTO participation = participationService.getParticipation(enchereId, utilisateurId);
            return ResponseEntity.ok(participation);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * Obtenir une participation par son ID
     * GET /api/participations/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getParticipationById(@PathVariable Long id) {
        try {
            ParticipationDTO participation = participationService.getParticipationById(id);
            return ResponseEntity.ok(participation);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * Obtenir le gagnant actuel d'une enchère (participation avec le montant le
     * plus élevé)
     * GET /api/participations/enchere/{encherId}/gagnant
     */
    @GetMapping("/enchere/{enchereId}/gagnant")
    public ResponseEntity<?> getGagnantActuel(@PathVariable Long enchereId) {
        try {
            ParticipationDTO gagnant = participationService.getGagnantActuel(enchereId);
            return ResponseEntity.ok(gagnant);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * Compter le nombre de participations pour une enchère
     * GET /api/participations/enchere/{encherId}/count
     */
    @GetMapping("/enchere/{enchereId}/count")
    public ResponseEntity<Long> countParticipations(@PathVariable Long enchereId) {
        long count = participationService.countParticipations(enchereId);
        return ResponseEntity.ok(count);
    }

    /**
     * Obtenir le montant maximum pour une enchère
     * GET /api/participations/enchere/{encherId}/montant-max
     */
    @GetMapping("/enchere/{enchereId}/montant-max")
    public ResponseEntity<Double> getMontantMax(@PathVariable Long enchereId) {
        Double montantMax = participationService.getMontantMax(enchereId);
        return ResponseEntity.ok(montantMax);
    }

    /**
     * Supprimer une participation par son ID
     * DELETE /api/participations/{participationId}
     */
    @DeleteMapping("/{participationId}")
    public ResponseEntity<?> supprimerParticipation(@PathVariable Long participationId) {
        try {
            participationService.supprimerParticipation(participationId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * Supprimer toutes les participations d'une enchère
     * DELETE /api/participations/enchere/{encherId}
     */
    @DeleteMapping("/enchere/{enchereId}")
    public ResponseEntity<Void> supprimerParticipationsByEnchere(@PathVariable Long enchereId) {
        participationService.supprimerParticipationsByEnchere(enchereId);
        return ResponseEntity.noContent().build();
    }
}
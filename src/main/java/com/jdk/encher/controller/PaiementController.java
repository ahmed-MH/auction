package com.jdk.encher.controller;

import com.jdk.encher.entity.Paiement;
import com.jdk.encher.entity.StatutPaiement;
import com.jdk.encher.entity.Utilisateur;
import com.jdk.encher.repository.PaiementRepository;
import com.jdk.encher.repository.UtilisateurRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/paiements")
@CrossOrigin(origins = "*") // autoriser les requêtes du front (React)
@Tag(name = "Paiement", description = "Gestion des paiements")
public class PaiementController {

    @Autowired
    private PaiementRepository paiementRepository;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    // 1. Lister tous les paiements
    @Operation(summary = "Récupérer tous les paiements")
    @GetMapping
    public List<Paiement> getAllPaiements() {
        return paiementRepository.findAll();
    }

    // 2. Récupérer un paiement par ID
    @Operation(summary = "Récupérer un paiement par ID")
    @GetMapping("/{id}")
    public ResponseEntity<?> getPaiementById(@PathVariable Long id) {
        Optional<Paiement> paiement = paiementRepository.findById(id);
        return paiement.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 3. Créer un nouveau paiement
    @Operation(summary = "Créer un nouveau paiement")
    @PostMapping
    public ResponseEntity<?> createPaiement(@RequestBody Paiement paiement) {
        // Vérifie si l'utilisateur existe
        if (paiement.getUtilisateur() != null && paiement.getUtilisateur().getId() != null) {
            Optional<Utilisateur> userOpt = utilisateurRepository.findById(paiement.getUtilisateur().getId());
            if (userOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Utilisateur introuvable !");
            }
            paiement.setUtilisateur(userOpt.get());
        }

        paiement.setDatePaiement(LocalDate.now()); // date automatique
        Paiement saved = paiementRepository.save(paiement);
        return ResponseEntity.ok(saved);
    }

    // 4. Mettre à jour un paiement
    @Operation(summary = "Mettre à jour un paiement existant")
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePaiement(@PathVariable Long id, @RequestBody Paiement newPaiement) {
        Optional<Paiement> existingPaiement = paiementRepository.findById(id);

        if (existingPaiement.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Paiement paiement = existingPaiement.get();
        paiement.setMontant(newPaiement.getMontant());
        paiement.setCommissionSite(newPaiement.getCommissionSite());
        paiement.setStatutPaiement(newPaiement.getStatutPaiement());

        Paiement updated = paiementRepository.save(paiement);
        return ResponseEntity.ok(updated);
    }

    // 5. Supprimer un paiement
    @Operation(summary = "Supprimer un paiement par ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePaiement(@PathVariable Long id) {
        if (!paiementRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        paiementRepository.deleteById(id);
        return ResponseEntity.ok("Paiement supprimé avec succès !");
    }

    // 6. Rechercher par statut
    @Operation(summary = "Rechercher les paiements par statut")
    @GetMapping("/statut/{statut}")
    public List<Paiement> getPaiementsByStatut(@PathVariable StatutPaiement statut) {
        return paiementRepository.findByStatutPaiement(statut);
    }

    // 7. Rechercher par date
    @Operation(summary = "Rechercher les paiements par date")
    @GetMapping("/date/{date}")
    public List<Paiement> getPaiementsByDate(@PathVariable String date) {
        LocalDate parsedDate = LocalDate.parse(date);
        return paiementRepository.findByDatePaiement(parsedDate);
    }

    // 8. Rechercher les paiements supérieurs à un montant donné
    @Operation(summary = "Rechercher les paiements supérieurs à un montant donné")
    @GetMapping("/montant/{montant}")
    public List<Paiement> getPaiementsByMontant(@PathVariable Double montant) {
        return paiementRepository.findByMontantGreaterThan(montant);
    }
}


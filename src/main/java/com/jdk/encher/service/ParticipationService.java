package com.jdk.encher.service;

import com.jdk.encher.dto.CreateParticipationDTO;
import com.jdk.encher.dto.ParticipationDTO;
import com.jdk.encher.dto.UpdateParticipationDTO;
import com.jdk.encher.entity.Enchere;
import com.jdk.encher.entity.Participation;
import com.jdk.encher.entity.Utilisateur;
import com.jdk.encher.repository.EnchereRepository;
import com.jdk.encher.repository.ParticipationRepository;
import com.jdk.encher.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ParticipationService {

    private final ParticipationRepository participationRepository;
    private final EnchereRepository enchereRepository;
    private final UtilisateurRepository utilisateurRepository;

    /**
     * Ajouter une nouvelle participation
     */
    @Transactional
    public ParticipationDTO ajouterParticipation(CreateParticipationDTO createDTO) {
        // Vérifier que l'enchère existe
        Enchere enchere = enchereRepository.findById(createDTO.getEnchereId())
                .orElseThrow(() -> new RuntimeException("Enchère non trouvée avec l'ID: " + createDTO.getEnchereId()));

        // Vérifier que l'utilisateur existe
        Utilisateur utilisateur = utilisateurRepository.findById(createDTO.getUtilisateurId())
                .orElseThrow(() -> new RuntimeException(
                        "Utilisateur non trouvé avec l'ID: " + createDTO.getUtilisateurId()));

        // Vérifier que le montant est supérieur au montant actuel
        if (createDTO.getMontant() <= enchere.getMontantActuel()) {
            throw new RuntimeException("Le montant (" + createDTO.getMontant() +
                    ") doit être supérieur au montant actuel (" + enchere.getMontantActuel() + ")");
        }

        // 1. Vérifier que l'utilisateur a assez de crédits
        if (utilisateur.getSoldeCredit() < createDTO.getMontant()) {
            throw new RuntimeException("Solde insuffisant ! Votre solde : " + utilisateur.getSoldeCredit());
        }

        // 2. Gérer le remboursement du précédent enchérisseur (si existe)
        List<Participation> participations = participationRepository.findByEnchereId(enchere.getId());

        if (!participations.isEmpty()) {
            // Trouver le meilleur enchérisseur actuel (celui avec le montant max)
            Participation bestParticipation = participations.stream()
                    .max((p1, p2) -> Double.compare(p1.getMontant(), p2.getMontant()))
                    .orElse(null);

            if (bestParticipation != null) {
                Utilisateur previousUser = bestParticipation.getUtilisateur();
                // Rembourser l'ancien utilisateur
                int montantRemboursement = bestParticipation.getMontant().intValue();
                previousUser.setSoldeCredit(previousUser.getSoldeCredit() + montantRemboursement);
                utilisateurRepository.save(previousUser);
            }
        }

        // 3. Déduire le montant du nouvel utilisateur
        utilisateur.setSoldeCredit(utilisateur.getSoldeCredit() - createDTO.getMontant().intValue());
        utilisateurRepository.save(utilisateur);

        // 4. Créer la participation
        Participation participation = Participation.builder()
                .enchere(enchere)
                .utilisateur(utilisateur)
                .montant(createDTO.getMontant())
                .build();

        // 5. Mettre à jour le montant actuel de l'enchère
        enchere.setMontantActuel(createDTO.getMontant());
        enchereRepository.save(enchere);

        Participation savedParticipation = participationRepository.save(participation);
        return convertToDTO(savedParticipation);
    }

    /**
     * Ajouter ou mettre à jour une participation (méthode simplifiée)
     */
    @Transactional
    public ParticipationDTO ajouterParticipation(Long enchereId, Long utilisateurId, Double montant) {
        CreateParticipationDTO createDTO = new CreateParticipationDTO(enchereId, utilisateurId, montant);
        return ajouterParticipation(createDTO);
    }

    /**
     * Mettre à jour le montant d'une participation existante
     */
    @Transactional
    public ParticipationDTO updateParticipation(Long participationId, UpdateParticipationDTO updateDTO) {
        Participation participation = participationRepository.findById(participationId)
                .orElseThrow(() -> new RuntimeException("Participation non trouvée avec l'ID: " + participationId));

        Enchere enchere = participation.getEnchere();

        // Vérifier que le nouveau montant est supérieur au montant actuel
        if (updateDTO.getMontant() <= enchere.getMontantActuel()) {
            throw new RuntimeException("Le nouveau montant doit être supérieur au montant actuel");
        }

        participation.setMontant(updateDTO.getMontant());
        enchere.setMontantActuel(updateDTO.getMontant());

        enchereRepository.save(enchere);
        Participation updatedParticipation = participationRepository.save(participation);

        return convertToDTO(updatedParticipation);
    }

    /**
     * Obtenir toutes les participations d'une enchère
     */
    @Transactional(readOnly = true)
    public List<ParticipationDTO> getParticipationsByEnchere(Long enchereId) {
        return participationRepository.findByEnchereId(enchereId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtenir toutes les participations d'un utilisateur
     */
    @Transactional(readOnly = true)
    public List<ParticipationDTO> getParticipationsByUtilisateur(Long utilisateurId) {
        return participationRepository.findByUtilisateurId(utilisateurId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtenir une participation spécifique (par enchère et utilisateur)
     */
    @Transactional(readOnly = true)
    public ParticipationDTO getParticipation(Long enchereId, Long utilisateurId) {
        Participation participation = participationRepository.findByEnchereIdAndUtilisateurId(enchereId, utilisateurId)
                .orElseThrow(
                        () -> new RuntimeException("Participation non trouvée pour cette enchère et cet utilisateur"));
        return convertToDTO(participation);
    }

    /**
     * Obtenir une participation par son ID
     */
    @Transactional(readOnly = true)
    public ParticipationDTO getParticipationById(Long id) {
        Participation participation = participationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Participation non trouvée avec l'ID: " + id));
        return convertToDTO(participation);
    }

    /**
     * Obtenir le gagnant actuel (participation avec le montant le plus élevé)
     */
    @Transactional(readOnly = true)
    public ParticipationDTO getGagnantActuel(Long enchereId) {
        List<Participation> participations = participationRepository.findTopParticipationsByEnchereId(enchereId);
        if (participations.isEmpty()) {
            throw new RuntimeException("Aucune participation trouvée pour cette enchère");
        }
        return convertToDTO(participations.get(0));
    }

    /**
     * Obtenir les dernières participations d'une enchère
     */
    @Transactional(readOnly = true)
    public List<ParticipationDTO> getRecentParticipations(Long enchereId) {
        return participationRepository.findRecentParticipationsByEnchereId(enchereId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Compter le nombre de participations pour une enchère
     */
    @Transactional(readOnly = true)
    public long countParticipations(Long enchereId) {
        return participationRepository.countByEnchereId(enchereId);
    }

    /**
     * Obtenir le montant maximum pour une enchère
     */
    @Transactional(readOnly = true)
    public Double getMontantMax(Long enchereId) {
        return participationRepository.findMaxMontantByEnchereId(enchereId)
                .orElse(0.0);
    }

    /**
     * Vérifier si un utilisateur a participé à une enchère
     */
    @Transactional(readOnly = true)
    public boolean hasUserParticipated(Long enchereId, Long utilisateurId) {
        return participationRepository.existsByEnchereIdAndUtilisateurId(enchereId, utilisateurId);
    }

    /**
     * Supprimer une participation
     */
    @Transactional
    public void supprimerParticipation(Long participationId) {
        if (!participationRepository.existsById(participationId)) {
            throw new RuntimeException("Participation non trouvée avec l'ID: " + participationId);
        }
        participationRepository.deleteById(participationId);
    }

    /**
     * Supprimer toutes les participations d'une enchère
     */
    @Transactional
    public void supprimerParticipationsByEnchere(Long enchereId) {
        participationRepository.deleteByEnchereId(enchereId);
    }

    /**
     * Convertir une entité Participation en DTO
     */
    private ParticipationDTO convertToDTO(Participation participation) {
        return ParticipationDTO.builder()
                .id(participation.getId())
                .enchereId(participation.getEnchere().getId())
                .nomProduit(participation.getEnchere().getNomProduit())
                .utilisateurId(participation.getUtilisateur().getId())
                .nomUtilisateur(participation.getUtilisateur().getNom())
                .emailUtilisateur(participation.getUtilisateur().getEmail())
                .montant(participation.getMontant())
                .dateParticipation(participation.getDateParticipation())
                .build();
    }
}
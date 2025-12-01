package com.jdk.encher.service;

import com.jdk.encher.dto.CreateParticipationDTO;
import com.jdk.encher.dto.ParticipationDTO;
import com.jdk.encher.dto.UpdateParticipationDTO;
import com.jdk.encher.entity.Encher;
import com.jdk.encher.entity.Participation;
import com.jdk.encher.entity.Utilisateur;
import com.jdk.encher.repository.EncherRepository;
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
    private final EncherRepository encherRepository;
    private final UtilisateurRepository utilisateurRepository;

    /**
     * Ajouter une nouvelle participation
     */
    @Transactional
    public ParticipationDTO ajouterParticipation(CreateParticipationDTO createDTO) {
        // Vérifier que l'enchère existe
        Encher encher = encherRepository.findById(createDTO.getEncherId())
                .orElseThrow(() -> new RuntimeException("Enchère non trouvée avec l'ID: " + createDTO.getEncherId()));

        // Vérifier que l'utilisateur existe
        Utilisateur utilisateur = utilisateurRepository.findById(createDTO.getUtilisateurId())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'ID: " + createDTO.getUtilisateurId()));

        // Vérifier que le montant est supérieur au montant actuel
        if (createDTO.getMontant() <= encher.getMontantActuel()) {
            throw new RuntimeException("Le montant (" + createDTO.getMontant() +
                    ") doit être supérieur au montant actuel (" + encher.getMontantActuel() + ")");
        }

        // Créer la participation
        Participation participation = Participation.builder()
                .encher(encher)
                .utilisateur(utilisateur)
                .montant(createDTO.getMontant())
                .build();

        // Mettre à jour le montant actuel de l'enchère
        encher.setMontantActuel(createDTO.getMontant());
        encherRepository.save(encher);

        Participation savedParticipation = participationRepository.save(participation);
        return convertToDTO(savedParticipation);
    }

    /**
     * Ajouter ou mettre à jour une participation (méthode simplifiée)
     */
    @Transactional
    public ParticipationDTO ajouterParticipation(Long encherId, Long utilisateurId, Double montant) {
        CreateParticipationDTO createDTO = new CreateParticipationDTO(encherId, utilisateurId, montant);
        return ajouterParticipation(createDTO);
    }

    /**
     * Mettre à jour le montant d'une participation existante
     */
    @Transactional
    public ParticipationDTO updateParticipation(Long participationId, UpdateParticipationDTO updateDTO) {
        Participation participation = participationRepository.findById(participationId)
                .orElseThrow(() -> new RuntimeException("Participation non trouvée avec l'ID: " + participationId));

        Encher encher = participation.getEncher();

        // Vérifier que le nouveau montant est supérieur au montant actuel
        if (updateDTO.getMontant() <= encher.getMontantActuel()) {
            throw new RuntimeException("Le nouveau montant doit être supérieur au montant actuel");
        }

        participation.setMontant(updateDTO.getMontant());
        encher.setMontantActuel(updateDTO.getMontant());

        encherRepository.save(encher);
        Participation updatedParticipation = participationRepository.save(participation);

        return convertToDTO(updatedParticipation);
    }

    /**
     * Obtenir toutes les participations d'une enchère
     */
    @Transactional(readOnly = true)
    public List<ParticipationDTO> getParticipationsByEncher(Long encherId) {
        return participationRepository.findByEncherId(encherId)
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
    public ParticipationDTO getParticipation(Long encherId, Long utilisateurId) {
        Participation participation = participationRepository.findByEncherIdAndUtilisateurId(encherId, utilisateurId)
                .orElseThrow(() -> new RuntimeException("Participation non trouvée pour cette enchère et cet utilisateur"));
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
    public ParticipationDTO getGagnantActuel(Long encherId) {
        List<Participation> participations = participationRepository.findTopParticipationsByEncherId(encherId);
        if (participations.isEmpty()) {
            throw new RuntimeException("Aucune participation trouvée pour cette enchère");
        }
        return convertToDTO(participations.get(0));
    }

    /**
     * Obtenir les dernières participations d'une enchère
     */
    @Transactional(readOnly = true)
    public List<ParticipationDTO> getRecentParticipations(Long encherId) {
        return participationRepository.findRecentParticipationsByEncherId(encherId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Compter le nombre de participations pour une enchère
     */
    @Transactional(readOnly = true)
    public long countParticipations(Long encherId) {
        return participationRepository.countByEncherId(encherId);
    }

    /**
     * Obtenir le montant maximum pour une enchère
     */
    @Transactional(readOnly = true)
    public Double getMontantMax(Long encherId) {
        return participationRepository.findMaxMontantByEncherId(encherId)
                .orElse(0.0);
    }

    /**
     * Vérifier si un utilisateur a participé à une enchère
     */
    @Transactional(readOnly = true)
    public boolean hasUserParticipated(Long encherId, Long utilisateurId) {
        return participationRepository.existsByEncherIdAndUtilisateurId(encherId, utilisateurId);
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
    public void supprimerParticipationsByEncher(Long encherId) {
        participationRepository.deleteByEncherId(encherId);
    }

    /**
     * Convertir une entité Participation en DTO
     */
    private ParticipationDTO convertToDTO(Participation participation) {
        return ParticipationDTO.builder()
                .id(participation.getId())
                .encherId(participation.getEncher().getId())
                .nomProduit(participation.getEncher().getNomProduit())
                .utilisateurId(participation.getUtilisateur().getId())
                .nomUtilisateur(participation.getUtilisateur().getNom())
                .emailUtilisateur(participation.getUtilisateur().getEmail())
                .montant(participation.getMontant())
                .dateParticipation(participation.getDateParticipation())
                .build();
    }
}
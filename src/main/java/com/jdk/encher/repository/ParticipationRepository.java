package com.jdk.encher.repository;

import com.jdk.encher.entity.Participation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParticipationRepository extends JpaRepository<Participation, Long> {

    // Trouver toutes les participations d'une enchère
    List<Participation> findByEnchereId(Long enchereId);

    // Trouver toutes les participations d'un utilisateur
    List<Participation> findByUtilisateurId(Long utilisateurId);

    // Vérifier si un utilisateur a déjà participé à une enchère
    boolean existsByEnchereIdAndUtilisateurId(Long enchereId, Long utilisateurId);

    // Trouver une participation spécifique
    Optional<Participation> findByEnchereIdAndUtilisateurId(Long enchereId, Long utilisateurId);

    // Trouver le montant le plus élevé pour une enchère
    @Query("SELECT MAX(p.montant) FROM Participation p WHERE p.enchere.id = :enchereId")
    Optional<Double> findMaxMontantByEnchereId(@Param("enchereId") Long enchereId);

    // Trouver les participations triées par montant décroissant
    @Query("SELECT p FROM Participation p WHERE p.enchere.id = :enchereId ORDER BY p.montant DESC")
    List<Participation> findTopParticipationsByEnchereId(@Param("enchereId") Long enchereId);

    // Compter le nombre de participants pour une enchère
    long countByEnchereId(Long enchereId);

    // Supprimer toutes les participations d'une enchère
    void deleteByEnchereId(Long enchereId);

    boolean existsByUtilisateurId(Long id);

    // Trouver les dernières participations d'une enchère
    @Query("SELECT p FROM Participation p WHERE p.enchere.id = :enchereId ORDER BY p.dateParticipation DESC")
    List<Participation> findRecentParticipationsByEnchereId(@Param("enchereId") Long enchereId);

    // Trouver les participations d'un utilisateur pour une enchère spécifique
    @Query("SELECT p FROM Participation p WHERE p.utilisateur.id = :utilisateurId AND p.enchere.id = :enchereId ORDER BY p.dateParticipation DESC")
    List<Participation> findByUtilisateurIdAndEnchereId(@Param("utilisateurId") Long utilisateurId,
            @Param("enchereId") Long enchereId);

    // Calculer la somme des montants bloqués (participations gagnantes en cours)
    @Query("SELECT SUM(p.montant) FROM Participation p JOIN p.enchere e WHERE p.utilisateur.id = :userId AND e.statut = 'EN_COURS' AND p.montant = e.montantActuel")
    Optional<Double> sumMontantBloqueByUtilisateurId(@Param("userId") Long userId);
}

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
    List<Participation> findByEncherId(Long encherId);

    // Trouver toutes les participations d'un utilisateur
    List<Participation> findByUtilisateurId(Long utilisateurId);

    // Vérifier si un utilisateur a déjà participé à une enchère
    boolean existsByEncherIdAndUtilisateurId(Long encherId, Long utilisateurId);

    // Trouver une participation spécifique
    Optional<Participation> findByEncherIdAndUtilisateurId(Long encherId, Long utilisateurId);

    // Trouver le montant le plus élevé pour une enchère
    @Query("SELECT MAX(p.montant) FROM Participation p WHERE p.encher.id = :encherId")
    Optional<Double> findMaxMontantByEncherId(@Param("encherId") Long encherId);

    // Trouver les participations triées par montant décroissant
    @Query("SELECT p FROM Participation p WHERE p.encher.id = :encherId ORDER BY p.montant DESC")
    List<Participation> findTopParticipationsByEncherId(@Param("encherId") Long encherId);

    // Compter le nombre de participants pour une enchère
    long countByEncherId(Long encherId);

    // Supprimer toutes les participations d'une enchère
    void deleteByEncherId(Long encherId);
    boolean existsByUtilisateurId(Long id);

    // Trouver les dernières participations d'une enchère
    @Query("SELECT p FROM Participation p WHERE p.encher.id = :encherId ORDER BY p.dateParticipation DESC")
    List<Participation> findRecentParticipationsByEncherId(@Param("encherId") Long encherId);

    // Trouver les participations d'un utilisateur pour une enchère spécifique
    @Query("SELECT p FROM Participation p WHERE p.utilisateur.id = :utilisateurId AND p.encher.id = :encherId ORDER BY p.dateParticipation DESC")
    List<Participation> findByUtilisateurIdAndEncherId(@Param("utilisateurId") Long utilisateurId, @Param("encherId") Long encherId);
}

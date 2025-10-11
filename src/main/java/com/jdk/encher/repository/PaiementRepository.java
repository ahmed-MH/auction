package com.jdk.encher.repository;

import com.jdk.encher.entity.Paiement;
import com.jdk.encher.entity.StatutPaiement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaiementRepository extends JpaRepository<Paiement, Long> {

    // Trouver tous les paiements par statut
    List<Paiement> findByStatutPaiement(StatutPaiement statutPaiement);

    // Trouver les paiements avec un montant supérieur à une valeur donnée
    List<Paiement> findByMontantGreaterThan(Double montant);

    // Trouver les paiements effectués à une date précise
    List<Paiement> findByDatePaiement(java.time.LocalDate datePaiement);
}

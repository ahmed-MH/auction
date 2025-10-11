package com.jdk.encher.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "paiements")
public class Paiement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double montant;

    private LocalDate datePaiement;

    private Double commissionSite;

    @Enumerated(EnumType.STRING)
    private StatutPaiement statutPaiement;

    @ManyToOne
    @JoinColumn(name = "utilisateur_id") // clé étrangère dans la table paiements
    private Utilisateur utilisateur;


    // Constructeurs
    public Paiement() {}

    public Paiement(Double montant, LocalDate datePaiement, Double commissionSite, StatutPaiement statutPaiement, Utilisateur utilisateur) {
        this.montant = montant;
        this.datePaiement = datePaiement;
        this.commissionSite = commissionSite;
        this.statutPaiement = statutPaiement;
        this.utilisateur = utilisateur;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getMontant() {
        return montant;
    }

    public void setMontant(Double montant) {
        this.montant = montant;
    }

    public LocalDate getDatePaiement() {
        return datePaiement;
    }

    public void setDatePaiement(LocalDate datePaiement) {
        this.datePaiement = datePaiement;
    }

    public Double getCommissionSite() {
        return commissionSite;
    }

    public void setCommissionSite(Double commissionSite) {
        this.commissionSite = commissionSite;
    }

    public StatutPaiement getStatutPaiement() {
        return statutPaiement;
    }

    public void setStatutPaiement(StatutPaiement statutPaiement) {
        this.statutPaiement = statutPaiement;
    }

    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }
}

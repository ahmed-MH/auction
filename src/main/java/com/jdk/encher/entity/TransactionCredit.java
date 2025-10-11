package com.jdk.encher.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions_credit")
public class TransactionCredit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private LocalDateTime dateTransaction;

    @Positive
    private double montant;

    @Positive
    private int nbCredits;

    @NotBlank
    private String modePaiement; // exemple : "Carte Bancaire", "PayPal", "Virement"

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutPaiement statut;

    @ManyToOne
    @JoinColumn( name = "utilisateur_id") // clé étrangère dans la table transactions_credit
    private Utilisateur utilisateur;

    // Constructeurs
    public TransactionCredit() {}

    public TransactionCredit(LocalDateTime dateTransaction, double montant, int nbCredits, String modePaiement, StatutPaiement statut, Utilisateur utilisateur) {
        this.dateTransaction = dateTransaction;
        this.montant = montant;
        this.nbCredits = nbCredits;
        this.modePaiement = modePaiement;
        this.statut = statut;
        this.utilisateur = utilisateur;
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDateTime getDateTransaction() { return dateTransaction; }
    public void setDateTransaction(LocalDateTime dateTransaction) { this.dateTransaction = dateTransaction; }

    public double getMontant() { return montant; }
    public void setMontant(double montant) { this.montant = montant; }

    public int getNbCredits() { return nbCredits; }
    public void setNbCredits(int nbCredits) { this.nbCredits = nbCredits; }

    public String getModePaiement() { return modePaiement; }
    public void setModePaiement(String modePaiement) { this.modePaiement = modePaiement; }

    public StatutPaiement getStatut() { return statut; }
    public void setStatut(StatutPaiement statut) { this.statut = statut; }

    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }
}
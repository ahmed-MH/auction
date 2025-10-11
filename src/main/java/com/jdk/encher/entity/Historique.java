package com.jdk.encher.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "historiques")
public class Historique {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Acheteur de la transaction
    @ManyToOne(optional = false)
    @JoinColumn(name = "acheteur_id", nullable = false)
    private Utilisateur acheteur;

    // Vendeur de la transaction
    @ManyToOne(optional = false)
    @JoinColumn(name = "vendeur_id", nullable = false)
    private Utilisateur vendeur;

    @Positive
    private double montant;

    @OneToOne
    @JoinColumn(name = "encher", unique = true)
    private Encher encher;

    // Constructeurs
    public Historique() {}

    public Historique(Utilisateur acheteur, Utilisateur vendeur, double montant, Encher encher) {
        this.acheteur = acheteur;
        this.vendeur = vendeur;
        this.montant = montant;
        this.encher = encher;
    }


    // Getters & setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Utilisateur getAcheteur() { return acheteur; }
    public void setAcheteur(Utilisateur acheteur) { this.acheteur = acheteur; }

    public Utilisateur getVendeur() { return vendeur; }
    public void setVendeur(Utilisateur vendeur) { this.vendeur = vendeur; }

    public double getMontant() { return montant; }
    public void setMontant(double montant) { this.montant = montant; }

    public Encher getEncher() {
        return encher;
    }
    public void setEncher(Encher encher) {
        this.encher = encher;
    }

}

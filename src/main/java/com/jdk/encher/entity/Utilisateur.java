package com.jdk.encher.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.util.List;

@Entity
@Table(name = "utilisateurs")
public class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 100)
    private String nom;

    @NotBlank
    @Email
    @Column(unique = true)
    private String email;

    @NotBlank
    @Size(min = 6)
    private String motDePasse;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    private int soldeCredit;

    private boolean etatCompte; // true = actif, false = désactivé

    @OneToMany(mappedBy = "createur")
    private List<Encher> enchersDefinies;

    @ManyToMany(mappedBy = "participants")
    private List<Encher> encheresParticipees;

    @OneToMany(mappedBy = "utilisateur")
    private List<Paiement> paiementsEffectues;

    @OneToMany(mappedBy = "utilisateur")
    private List<Notification> notificationRecues;

    @OneToMany(mappedBy = "utilisateur")
    private List<TransactionCredit> transactionsEffectuees;


    // Constructeurs
    public Utilisateur() {}

    public Utilisateur(String nom, String email, String motDePasse, Role role, int soldeCredit, boolean etatCompte) {
        this.nom = nom;
        this.email = email;
        this.motDePasse = motDePasse;
        this.role = role;
        this.soldeCredit = soldeCredit;
        this.etatCompte = etatCompte;
    }

    public Utilisateur(String nom, String email, String motDePasse, Role role, int soldeCredit, boolean etatCompte, List<Encher> enchersDefinies, List<Encher> encheresParticipees, List<Paiement> paiementsEffectues, List<Notification> notificationRecues, List<TransactionCredit> transactionsEffectuees) {
        this.nom = nom;
        this.email = email;
        this.motDePasse = motDePasse;
        this.role = role;
        this.soldeCredit = soldeCredit;
        this.etatCompte = etatCompte;
        this.enchersDefinies = enchersDefinies;
        this.encheresParticipees = encheresParticipees;
        this.paiementsEffectues = paiementsEffectues;
        this.notificationRecues = notificationRecues;
        this.transactionsEffectuees = transactionsEffectuees;
    }

    // Getters & setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getMotDePasse() { return motDePasse; }
    public void setMotDePasse(String motDePasse) { this.motDePasse = motDePasse; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public int getSoldeCredit() { return soldeCredit; }
    public void setSoldeCredit(int soldeCredit) { this.soldeCredit = soldeCredit; }

    public boolean isEtatCompte() { return etatCompte; }
    public void setEtatCompte(boolean etatCompte) { this.etatCompte = etatCompte; }

    public List<Encher> getEnchersDefinies() {
        return enchersDefinies;
    }

    public void setEnchersDefinies(List<Encher> enchersDefinies) {
        this.enchersDefinies = enchersDefinies;
    }

    public List<Encher> getEncheresParticipees() {
        return encheresParticipees;
    }

    public void setEncheresParticipees(List<Encher> encheresParticipees) {
        this.encheresParticipees = encheresParticipees;
    }

    public List<Paiement> getPaiementsEffectues() {
        return paiementsEffectues;
    }

    public void setPaiementsEffectues(List<Paiement> paiementsEffectues) {
        this.paiementsEffectues = paiementsEffectues;
    }

    public List<Notification> getNotificationRecues() {
        return notificationRecues;
    }

    public void setNotificationRecues(List<Notification> notificationRecues) {
        this.notificationRecues = notificationRecues;
    }

    public List<TransactionCredit> getTransactionsEffectuees() {
        return transactionsEffectuees;
    }

    public void setTransactionsEffectuees(List<TransactionCredit> transactionsEffectuees) {
        this.transactionsEffectuees = transactionsEffectuees;
    }
}


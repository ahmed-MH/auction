package com.jdk.encher.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "enchers")
public class Encher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime dateEncher;  // moment où l’enchère a été placée

    @NotNull
    private LocalDateTime dateDebut;   // date d’ouverture

    @NotNull
    private LocalDateTime dateFin;     // date de clôture

    @NotNull
    private String nomProduit;

    @NotNull
    private String description;

    @NotNull
    private String image;

    @NotNull
    private double prixDepart;

    @NotNull
    private double montantActuel;        // montant de l’enchère

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutEncher statut;

    @ManyToOne
    @JoinColumn(name = "gagnant_id", nullable = true)
    private Utilisateur gagnant;


    @ManyToOne
    @JoinColumn(name = "createur_id")  // clé étrangère dans la table enchers
    private Utilisateur createur;

    @ManyToMany
    @JoinTable(
            name = "participants_encheres",  // table d’association
            joinColumns = @JoinColumn(name = "encher_id"),
            inverseJoinColumns = @JoinColumn(name = "utilisateur_id")
    )
    private List<Utilisateur> participants;

    @OneToOne(mappedBy = "encher", cascade= CascadeType.ALL)
    private Historique historique;

    // Constructeurs
    public Encher() {}

    public Encher(LocalDateTime dateEncher, LocalDateTime dateDebut, LocalDateTime dateFin, String nomProduit, String description, String image, double prixDepart, double montantActuel, StatutEncher statut, Utilisateur gagnant, Utilisateur createur, List<Utilisateur> participants, Historique historique) {
        this.dateEncher = dateEncher;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.nomProduit = nomProduit;
        this.description = description;
        this.image = image;
        this.prixDepart = prixDepart;
        this.montantActuel = montantActuel;
        this.statut = statut;
        this.gagnant = gagnant;
        this.createur = createur;
        this.participants = participants;
        this.historique = historique;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getDateEncher() {
        return dateEncher;
    }

    public void setDateEncher(LocalDateTime dateEncher) {
        this.dateEncher = dateEncher;
    }

    public LocalDateTime getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(LocalDateTime dateDebut) {
        this.dateDebut = dateDebut;
    }

    public LocalDateTime getDateFin() {
        return dateFin;
    }

    public void setDateFin(LocalDateTime dateFin) {
        this.dateFin = dateFin;
    }

    public String getNomProduit() {
        return nomProduit;
    }

    public void setNomProduit(String nomProduit) {
        this.nomProduit = nomProduit;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public double getPrixDepart() {
        return prixDepart;
    }

    public void setPrixDepart(double prixDepart) {
        this.prixDepart = prixDepart;
    }

    public double getMontantActuel() {
        return montantActuel;
    }

    public void setMontantActuel(double montantActuel) {
        this.montantActuel = montantActuel;
    }

    public StatutEncher getStatut() {
        return statut;
    }

    public void setStatut(StatutEncher statut) {
        this.statut = statut;
    }

    public Utilisateur getGagnant() {
        return gagnant;
    }

    public void setGagnant(Utilisateur gagnant) {
        this.gagnant = gagnant;
    }

    public Utilisateur getCreateur() {
        return createur;
    }

    public void setCreateur(Utilisateur createur) {
        this.createur = createur;
    }

    public List<Utilisateur> getParticipants() {
        return participants;
    }

    public void setParticipants(List<Utilisateur> participants) {
        this.participants = participants;
    }

    public Historique getHistorique() {
        return historique;
    }

    public void setHistorique(Historique historique) {
        this.historique = historique;
    }
}
package com.jdk.encher.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String type; // EMAIL, SMS, SYSTEME...

    @NotBlank
    @Size(max = 2000)
    private String message;

    @NotNull
    private LocalDateTime dateEnvoi;

    private boolean statut; // true = envoyée, false = en attente

    @ManyToOne
    @JoinColumn(name = "utilisateur_id") // clé étrangère dans la table notifications
    private Utilisateur utilisateur;

    // Constructeurs
    public Notification() {}

    public Notification(String type, String message, LocalDateTime dateEnvoi, boolean statut, Utilisateur utilisateur) {
        this.type = type;
        this.message = message;
        this.dateEnvoi = dateEnvoi;
        this.statut = statut;
        this.utilisateur = utilisateur;
    }

    // Getters & setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public LocalDateTime getDateEnvoi() { return dateEnvoi; }
    public void setDateEnvoi(LocalDateTime dateEnvoi) { this.dateEnvoi = dateEnvoi; }

    public boolean isStatut() { return statut; }
    public void setStatut(boolean statut) { this.statut = statut; }

    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }
}

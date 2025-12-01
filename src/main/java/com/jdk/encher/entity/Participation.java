package com.jdk.encher.entity;

import com.jdk.encher.entity.Encher;
import com.jdk.encher.entity.Utilisateur;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "participations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Participation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "encher_id", nullable = false)
    @NotNull
    private Encher encher;

    @ManyToOne
    @JoinColumn(name = "utilisateur_id", nullable = false)
    @NotNull
    private Utilisateur utilisateur;

    @NotNull
    @Column(nullable = false)
    private Double montant;

    @Column(nullable = false)
    private LocalDateTime dateParticipation;

    @PrePersist
    protected void onCreate() {
        dateParticipation = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Encher getEncher() {
        return encher;
    }

    public void setEncher(Encher encher) {
        this.encher = encher;
    }

    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }

    public Double getMontant() {
        return montant;
    }

    public void setMontant(Double montant) {
        this.montant = montant;
    }

    public LocalDateTime getDateParticipation() {
        return dateParticipation;
    }

    public void setDateParticipation(LocalDateTime dateParticipation) {
        this.dateParticipation = dateParticipation;
    }
}
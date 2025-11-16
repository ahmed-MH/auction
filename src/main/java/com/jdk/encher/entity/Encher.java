package com.jdk.encher.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "enchers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Encher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime dateEncher;

    @NotNull
    private LocalDateTime dateDebut;

    @NotNull
    private LocalDateTime dateFin;

    @NotNull
    private String nomProduit;

    @NotNull
    private String description;

    @NotNull
    private double prixDepart;

    @NotNull
    private double montantActuel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutEncher statut;

    @ManyToOne
    @JoinColumn(name = "categorie_id", nullable = false)
    private Categorie categorie;

    @ManyToOne
    @JoinColumn(name = "gagnant_id", nullable = true)
    private Utilisateur gagnant;

    @ManyToOne
    @JoinColumn(name = "createur_id")
    private Utilisateur createur;

    @ManyToMany
    @JoinTable(
            name = "participants_encheres",
            joinColumns = @JoinColumn(name = "encher_id"),
            inverseJoinColumns = @JoinColumn(name = "utilisateur_id")
    )
    private List<Utilisateur> participants;

    @OneToOne(mappedBy = "encher", cascade = CascadeType.ALL)
    private Historique historique;

    @OneToMany(mappedBy = "encher", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Image> images;
}
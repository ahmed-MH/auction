package com.jdk.encher.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "images")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String url; // le chemin ou nom du fichier de l’image

    @ManyToOne
    @JoinColumn(name = "encher_id")
    private Encher encher;
}
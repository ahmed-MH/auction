package com.jdk.encher.dto;

import com.jdk.encher.entity.Categorie;
import com.jdk.encher.entity.StatutEnchere;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class EnchereResponseDTO {

    private Long id;

    private String nomProduit;
    private String description;

    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;

    private Double prixDepart;
    private Double montantActuel;

    private StatutEnchere statut;

    private Categorie categorie;
    private Long categorieId;
    private Long createurId;
    private Long gagnantId;

    private List<String> imageUrls;
}
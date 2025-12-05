package com.jdk.encher.dto;

import com.jdk.encher.entity.StatutEnchere;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class EnchereUpdateDTO {
    private String nomProduit;
    private String description;
    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
    private Double prixDepart;
    private Double montantActuel;
    private StatutEnchere statut;
    private Long categorieId;
    private Long gagnantId;
    private List<ImageDTO> images;
}
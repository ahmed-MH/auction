package com.jdk.encher.dto;

import com.jdk.encher.entity.StatutEncher;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class EncherUpdateDTO {
    private String nomProduit;
    private String description;
    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
    private Double prixDepart;
    private Double montantActuel;
    private StatutEncher statut;
    private Long categorieId;
    private Long gagnantId; // optional
}
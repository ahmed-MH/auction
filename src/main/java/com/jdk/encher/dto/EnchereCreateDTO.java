package com.jdk.encher.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class EnchereCreateDTO {
    private String nomProduit;
    private String description;
    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
    private Double prixDepart;
    private Long categorieId;
    private Long createurId;
    private List<ImageDTO> images;
}
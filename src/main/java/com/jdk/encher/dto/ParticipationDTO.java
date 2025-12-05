package com.jdk.encher.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParticipationDTO {
    private Long id;
    private Long enchereId;
    private String nomProduit;
    private Long utilisateurId;
    private String nomUtilisateur;
    private String emailUtilisateur;
    private Double montant;
    private LocalDateTime dateParticipation;
}
package com.jdk.encher.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateParticipationDTO {

    @NotNull(message = "L'ID de l'enchère est obligatoire")
    private Long encherId;

    @NotNull(message = "L'ID de l'utilisateur est obligatoire")
    private Long utilisateurId;

    @NotNull(message = "Le montant est obligatoire")
    @Positive(message = "Le montant doit être positif")
    private Double montant;
}
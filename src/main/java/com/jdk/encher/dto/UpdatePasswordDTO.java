package com.jdk.encher.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdatePasswordDTO {
    @NotBlank(message = "Ancien mot de passe requis")
    private String ancienMotDePasse;

    @NotBlank(message = "Nouveau mot de passe requis")
    @Size(min = 6, message = "Le mot de passe doit faire au moins 6 caractères")
    private String nouveauMotDePasse;
}


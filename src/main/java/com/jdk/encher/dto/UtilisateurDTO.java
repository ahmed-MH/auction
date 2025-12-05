package com.jdk.encher.dto;

import com.jdk.encher.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UtilisateurDTO {
    private Long id;
    private String nom;
    private String email;
    private Role role;
    private int soldeCredit;
    private boolean etatCompte;
}
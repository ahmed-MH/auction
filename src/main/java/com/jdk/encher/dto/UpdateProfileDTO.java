package com.jdk.encher.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateProfileDTO {
    @Size(max = 100)
    private String nom;

    @Email
    private String email;
}

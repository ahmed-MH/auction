package com.jdk.encher.service;

import com.jdk.encher.dto.UpdatePasswordDTO;
import com.jdk.encher.dto.UpdateProfileDTO;
import com.jdk.encher.entity.Utilisateur;
import com.jdk.encher.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UtilisateurService {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;

    public Utilisateur updateProfile(Long userId, UpdateProfileDTO dto) {
        Utilisateur user = utilisateurRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        if (dto.getNom() != null) user.setNom(dto.getNom());
        if (dto.getEmail() != null) user.setEmail(dto.getEmail());

        return utilisateurRepository.save(user);
    }

    public void updatePassword(Long userId, UpdatePasswordDTO dto) {
        Utilisateur user = utilisateurRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        if (dto.getAncienMotDePasse() == null || dto.getNouveauMotDePasse() == null) {
            throw new RuntimeException("Les champs de mot de passe ne peuvent pas être vides");
        }

        if (!passwordEncoder.matches(dto.getAncienMotDePasse(), user.getMotDePasse())) {
            throw new RuntimeException("Ancien mot de passe incorrect");
        }

        user.setMotDePasse(passwordEncoder.encode(dto.getNouveauMotDePasse()));
        utilisateurRepository.save(user);
    }


    public void deleteAccount(Long userId) {
        if (!utilisateurRepository.existsById(userId)) {
            throw new RuntimeException("Utilisateur non trouvé");
        }
        utilisateurRepository.deleteById(userId);
    }
}

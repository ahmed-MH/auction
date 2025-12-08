package com.jdk.encher.service;

import com.jdk.encher.dto.StatsDTO;
import com.jdk.encher.dto.UtilisateurDTO;
import com.jdk.encher.entity.Utilisateur;
import com.jdk.encher.repository.UtilisateurRepository;
import com.jdk.encher.repository.EnchereRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UtilisateurRepository utilisateurRepository;
    private final EnchereRepository enchereRepository;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    public void createAdmin(com.jdk.encher.dto.SignUpRequest dto) {
        if (utilisateurRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email déjà utilisé !");
        }

        Utilisateur admin = new Utilisateur();
        admin.setNom(dto.getNom());
        admin.setEmail(dto.getEmail());
        admin.setMotDePasse(passwordEncoder.encode(dto.getPassword()));
        admin.setRole(com.jdk.encher.entity.Role.ADMIN);
        admin.setEtatCompte(true);
        admin.setSoldeCredit(0);

        utilisateurRepository.save(admin);
    }

    public StatsDTO getStatistics() {
        long totalUsers = utilisateurRepository.count();
        long totalProducts = enchereRepository.count();
        double totalRevenue = 0.0; // À calculer selon votre logique
        int creditsPurchased = 0; // À calculer selon votre logique

        return StatsDTO.builder()
                .totalUsers(totalUsers)
                .totalProducts(totalProducts)
                .totalRevenue(totalRevenue)
                .creditsPurchased(creditsPurchased)
                .build();
    }

    public List<UtilisateurDTO> getAllUsers() {
        return utilisateurRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public void updateUserStatus(Long id, Boolean etatCompte) {
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        utilisateur.setEtatCompte(etatCompte);
        utilisateurRepository.save(utilisateur);
    }

    private UtilisateurDTO convertToDTO(Utilisateur utilisateur) {
        return UtilisateurDTO.builder()
                .id(utilisateur.getId())
                .nom(utilisateur.getNom())
                .email(utilisateur.getEmail())
                .role(utilisateur.getRole())
                .soldeCredit(utilisateur.getSoldeCredit())
                .etatCompte(utilisateur.isEtatCompte())
                .build();
    }
}
package com.jdk.encher.service;

import com.jdk.encher.dto.StatsDTO;
import com.jdk.encher.dto.UtilisateurDTO;
import com.jdk.encher.entity.Utilisateur;
import com.jdk.encher.repository.UtilisateurRepository;
import com.jdk.encher.repository.EncherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UtilisateurRepository utilisateurRepository;
    private final EncherRepository encherRepository;

    public StatsDTO getStatistics() {
        long totalUsers = utilisateurRepository.count();
        long totalProducts = encherRepository.count();
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
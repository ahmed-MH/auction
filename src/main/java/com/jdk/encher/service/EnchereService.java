package com.jdk.encher.service;

import com.jdk.encher.dto.EnchereCreateDTO;
import com.jdk.encher.dto.EnchereResponseDTO;
import com.jdk.encher.dto.EnchereUpdateDTO;
import com.jdk.encher.dto.ImageDTO;
import com.jdk.encher.entity.*;
import com.jdk.encher.repository.CategorieRepository;
import com.jdk.encher.repository.EnchereRepository;
import com.jdk.encher.repository.ImageRepository;
import com.jdk.encher.repository.UtilisateurRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class EnchereService {

    private final CloudinaryService cloudinaryService;
    private final EnchereRepository enchereRepository;
    private final ImageRepository imageRepository;
    private final CategorieRepository categorieRepository;
    private final UtilisateurRepository utilisateurRepository;

    // ------------------------------------------------------------
    // 🔹 Convert Entity → DTO
    // ------------------------------------------------------------
    private EnchereResponseDTO toDto(Enchere enchere) {
        return EnchereResponseDTO.builder()
                .id(enchere.getId())
                .nomProduit(enchere.getNomProduit())
                .description(enchere.getDescription())
                .dateDebut(enchere.getDateDebut())
                .dateFin(enchere.getDateFin())
                .prixDepart(enchere.getPrixDepart())
                .montantActuel(enchere.getMontantActuel())
                .statut(enchere.getStatut())
                .categorie(enchere.getCategorie())
                .createurId(enchere.getCreateur() != null ? enchere.getCreateur().getId() : null)
                .gagnantId(enchere.getGagnant() != null ? enchere.getGagnant().getId() : null)
                // ✅ ADD THIS: Map images to imageUrls
                .imageUrls(enchere.getImages() != null
                        ? enchere.getImages().stream()
                                .map(Image::getUrl)
                                .collect(Collectors.toList())
                        : new ArrayList<>())
                .build();
    }

    public EnchereResponseDTO updateEnchereDTO(Long id, EnchereUpdateDTO dto) {
        Enchere updated = updateEnchere(id, dto);
        return toDto(updated);
    }

    // ------------------------------------------------------------
    // 🔹 Récupérer tout
    // ------------------------------------------------------------
    @Transactional(readOnly = true)
    public List<EnchereResponseDTO> getAllEncheres() {
        return enchereRepository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    // ------------------------------------------------------------
    // 🔹 Récupérer par ID
    // ------------------------------------------------------------
    @Transactional(readOnly = true)
    public EnchereResponseDTO getEnchereById(Long id) {
        return enchereRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Enchère introuvable ID : " + id));
    }

    // ------------------------------------------------------------
    // 🔹 Créer enchère
    // ------------------------------------------------------------
    public EnchereResponseDTO createEnchere(EnchereCreateDTO dto) {

        Categorie categorie = categorieRepository.findById(dto.getCategorieId())
                .orElseThrow(() -> new EntityNotFoundException("Catégorie introuvable"));

        Utilisateur createur = utilisateurRepository.findById(dto.getCreateurId())
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur créateur introuvable"));

        Enchere enchere = Enchere.builder()
                .nomProduit(dto.getNomProduit())
                .description(dto.getDescription())
                .dateDebut(dto.getDateDebut())
                .dateFin(dto.getDateFin())
                .prixDepart(dto.getPrixDepart())
                .montantActuel(dto.getPrixDepart())
                .statut(StatutEnchere.EN_COURS)
                .categorie(categorie)
                .createur(createur)
                .build();

        Enchere saved = enchereRepository.save(enchere);

        // ✅ ADD THIS: Save image URLs from Cloudinary
        if (dto.getImages() != null && !dto.getImages().isEmpty()) {
            List<Image> images = new ArrayList<>();
            for (ImageDTO imageDTO : dto.getImages()) { // ✅ Changed from Image to ImageDTO
                Image img = Image.builder()
                        .url(imageDTO.getUrl())
                        .enchere(saved)
                        .build();
                images.add(imageRepository.save(img));
            }
            saved.setImages(images);
        }

        return toDto(saved);
    }

    // ------------------------------------------------------------
    // 🔹 Update enchère
    // ------------------------------------------------------------
    public Enchere updateEnchere(Long id, EnchereUpdateDTO dto) {

        Enchere existing = enchereRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Encher not found"));

        // 🔹 Mise à jour des champs simples
        if (dto.getNomProduit() != null)
            existing.setNomProduit(dto.getNomProduit());
        if (dto.getDescription() != null)
            existing.setDescription(dto.getDescription());
        if (dto.getDateDebut() != null)
            existing.setDateDebut(dto.getDateDebut());
        if (dto.getDateFin() != null)
            existing.setDateFin(dto.getDateFin());
        if (dto.getPrixDepart() != null) {
            existing.setPrixDepart(dto.getPrixDepart());
            existing.setMontantActuel(dto.getPrixDepart()); // 🔹 MontantActuel = PrixDepart
        }
        // Ne pas prendre le montantActuel depuis le DTO, il sera toujours égal au
        // prixDepart
        // if (dto.getMontantActuel() != null)
        // existing.setMontantActuel(dto.getMontantActuel());

        if (dto.getStatut() != null)
            existing.setStatut(dto.getStatut());

        // 🔹 Catégorie
        if (dto.getCategorieId() != null) {
            Categorie cat = categorieRepository.findById(dto.getCategorieId())
                    .orElseThrow(() -> new RuntimeException("Categorie not found"));
            existing.setCategorie(cat);
        }

        // 🔹 Gagnant
        if (dto.getGagnantId() != null) {
            Utilisateur gagnant = utilisateurRepository.findById(dto.getGagnantId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            existing.setGagnant(gagnant);
        }

        // 🔥 Mise à jour des IMAGES
        if (dto.getImages() != null) {

            // Supprimer les anciennes images
            if (existing.getImages() != null) {
                imageRepository.deleteAll(existing.getImages());
                existing.getImages().clear(); // Vider la collection Hibernate
            }

            // Ajouter les nouvelles images
            for (ImageDTO imgDto : dto.getImages()) {
                if (imgDto.getUrl() == null || imgDto.getUrl().isEmpty())
                    continue;
                Image img = new Image();
                img.setUrl(imgDto.getUrl());
                img.setEnchere(existing); // Relation bidirectionnelle
                imageRepository.save(img);
                existing.getImages().add(img);
            }
        }

        return enchereRepository.save(existing);
    }

    // ------------------------------------------------------------
    // 🔹 Delete enchère
    // ------------------------------------------------------------
    public void deleteEnchere(Long id) {
        Enchere existing = enchereRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Enchère introuvable ID : " + id));

        imageRepository.deleteAll(existing.getImages());
        enchereRepository.delete(existing);
    }

    // ------------------------------------------------------------
    // 🔹 Lister les images
    // ------------------------------------------------------------
    @Transactional(readOnly = true)
    public List<String> getImagesByEnchere(Long encherId) {

        Enchere e = enchereRepository.findById(encherId)
                .orElseThrow(() -> new EntityNotFoundException("Enchère introuvable"));

        return e.getImages().stream().map(Image::getUrl).toList();
    }

    public List<EnchereResponseDTO> getEncheresByCategorie(Long categorieId) {
        return enchereRepository.findByCategorieId(categorieId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    public List<EnchereResponseDTO> getEnchersByUtilisateur(Long userId) {
        return enchereRepository.findByCreateurId(userId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public EnchereResponseDTO checkAndCloseEnchere(Long encherId) {
        Enchere enchere = enchereRepository.findById(encherId)
                .orElseThrow(() -> new EntityNotFoundException("Enchère introuvable ID : " + encherId));

        // Vérifier si l'enchère est déjà terminée
        if (enchere.getDateFin().isBefore(LocalDateTime.now()) && enchere.getStatut() != StatutEnchere.TERMINEE) {
            enchere.setStatut(StatutEnchere.TERMINEE);

            // Déterminer le gagnant (participation avec le plus grand montant)
            Participation gagnantParticipation = enchere.getParticipations().stream()
                    .max((p1, p2) -> Double.compare(p1.getMontant(), p2.getMontant()))
                    .orElse(null);

            if (gagnantParticipation != null) {
                Utilisateur gagnant = gagnantParticipation.getUtilisateur();
                enchere.setGagnant(gagnant);

                // 💰 TRANSFERT DES CRÉDITS AU VENDEUR (CRÉATEUR)
                Utilisateur vendeur = enchere.getCreateur();
                if (vendeur != null) {
                    // Le montant a déjà été déduit du gagnant lors de l'enchère ("bloqué")
                    // On ne fait que le transférer au vendeur
                    double montantFinal = gagnantParticipation.getMontant();
                    vendeur.setSoldeCredit(vendeur.getSoldeCredit() + (int) montantFinal);
                    utilisateurRepository.save(vendeur);
                }
            }

            enchereRepository.save(enchere);
        }

        return toDto(enchere);
    }

}

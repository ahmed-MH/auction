package com.jdk.encher.service;

import com.jdk.encher.dto.EncherCreateDTO;
import com.jdk.encher.dto.EncherResponseDTO;
import com.jdk.encher.dto.EncherUpdateDTO;
import com.jdk.encher.dto.ImageDTO;
import com.jdk.encher.entity.*;
import com.jdk.encher.repository.CategorieRepository;
import com.jdk.encher.repository.EncherRepository;
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
public class EncherService {

    private final CloudinaryService cloudinaryService;
    private final EncherRepository encherRepository;
    private final ImageRepository imageRepository;
    private final CategorieRepository categorieRepository;
    private final UtilisateurRepository utilisateurRepository;

    // ------------------------------------------------------------
    // 🔹 Convert Entity → DTO
    // ------------------------------------------------------------
    private EncherResponseDTO toDto(Encher encher) {
        return EncherResponseDTO.builder()
                .id(encher.getId())
                .nomProduit(encher.getNomProduit())
                .description(encher.getDescription())
                .dateDebut(encher.getDateDebut())
                .dateFin(encher.getDateFin())
                .prixDepart(encher.getPrixDepart())
                .montantActuel(encher.getMontantActuel())
                .statut(encher.getStatut())
                .categorie(encher.getCategorie())
                .createurId(encher.getCreateur() != null ? encher.getCreateur().getId() : null)
                .gagnantId(encher.getGagnant() != null ? encher.getGagnant().getId() : null)
                // ✅ ADD THIS: Map images to imageUrls
                .imageUrls(encher.getImages() != null
                        ? encher.getImages().stream()
                        .map(Image::getUrl)
                        .collect(Collectors.toList())
                        : new ArrayList<>())
                .build();
    }
    public EncherResponseDTO updateEnchereDTO(Long id, EncherUpdateDTO dto) {
        Encher updated = updateEnchere(id, dto);
        return toDto(updated);
    }
    // ------------------------------------------------------------
    // 🔹 Récupérer tout
    // ------------------------------------------------------------
    @Transactional(readOnly = true)
    public List<EncherResponseDTO> getAllEncheres() {
        return encherRepository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    // ------------------------------------------------------------
    // 🔹 Récupérer par ID
    // ------------------------------------------------------------
    @Transactional(readOnly = true)
    public EncherResponseDTO getEnchereById(Long id) {
        return encherRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Enchère introuvable ID : " + id));
    }

    // ------------------------------------------------------------
    // 🔹 Créer enchère
    // ------------------------------------------------------------
    public EncherResponseDTO createEnchere(EncherCreateDTO dto) {

        Categorie categorie = categorieRepository.findById(dto.getCategorieId())
                .orElseThrow(() -> new EntityNotFoundException("Catégorie introuvable"));

        Utilisateur createur = utilisateurRepository.findById(dto.getCreateurId())
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur créateur introuvable"));

        Encher encher = Encher.builder()
                .nomProduit(dto.getNomProduit())
                .description(dto.getDescription())
                .dateDebut(dto.getDateDebut())
                .dateFin(dto.getDateFin())
                .prixDepart(dto.getPrixDepart())
                .montantActuel(dto.getPrixDepart())
                .statut(StatutEncher.EN_COURS)
                .categorie(categorie)
                .createur(createur)
                .build();

        Encher saved = encherRepository.save(encher);

        // ✅ ADD THIS: Save image URLs from Cloudinary
        if (dto.getImages() != null && !dto.getImages().isEmpty()) {
            List<Image> images = new ArrayList<>();
            for (ImageDTO imageDTO : dto.getImages()) { // ✅ Changed from Image to ImageDTO
                Image img = Image.builder()
                        .url(imageDTO.getUrl())
                        .encher(saved)
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
    public Encher updateEnchere(Long id, EncherUpdateDTO dto) {

        Encher existing = encherRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Encher not found"));

        // 🔹 Mise à jour des champs simples
        if (dto.getNomProduit() != null) existing.setNomProduit(dto.getNomProduit());
        if (dto.getDescription() != null) existing.setDescription(dto.getDescription());
        if (dto.getDateDebut() != null) existing.setDateDebut(dto.getDateDebut());
        if (dto.getDateFin() != null) existing.setDateFin(dto.getDateFin());
        if (dto.getPrixDepart() != null) {
            existing.setPrixDepart(dto.getPrixDepart());
            existing.setMontantActuel(dto.getPrixDepart()); // 🔹 MontantActuel = PrixDepart
        }
        // Ne pas prendre le montantActuel depuis le DTO, il sera toujours égal au prixDepart
        // if (dto.getMontantActuel() != null) existing.setMontantActuel(dto.getMontantActuel());

        if (dto.getStatut() != null) existing.setStatut(dto.getStatut());

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
                if (imgDto.getUrl() == null || imgDto.getUrl().isEmpty()) continue;
                Image img = new Image();
                img.setUrl(imgDto.getUrl());
                img.setEncher(existing); // Relation bidirectionnelle
                imageRepository.save(img);
                existing.getImages().add(img);
            }
        }

        return encherRepository.save(existing);
    }


    // ------------------------------------------------------------
    // 🔹 Delete enchère
    // ------------------------------------------------------------
    public void deleteEnchere(Long id) {
        Encher existing = encherRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Enchère introuvable ID : " + id));

        imageRepository.deleteAll(existing.getImages());
        encherRepository.delete(existing);
    }

    // ------------------------------------------------------------
    // 🔹 Lister les images
    // ------------------------------------------------------------
    @Transactional(readOnly = true)
    public List<String> getImagesByEnchere(Long encherId) {

        Encher e = encherRepository.findById(encherId)
                .orElseThrow(() -> new EntityNotFoundException("Enchère introuvable"));

        return e.getImages().stream().map(Image::getUrl).toList();
    }

    public List<EncherResponseDTO> getEncheresByCategorie(Long categorieId) {
        return encherRepository.findByCategorieId(categorieId)
                .stream()
                .map(this::toDto)
                .toList();
    }
    public List<EncherResponseDTO> getEnchersByUtilisateur(Long userId) {
        return encherRepository.findByCreateurId(userId)
                .stream()
                .map(this::toDto)
                .toList();
    }
    @Transactional
    public EncherResponseDTO checkAndCloseEnchere(Long encherId) {
        Encher encher = encherRepository.findById(encherId)
                .orElseThrow(() -> new EntityNotFoundException("Enchère introuvable ID : " + encherId));

        // Vérifier si l'enchère est déjà terminée
        if (encher.getDateFin().isBefore(LocalDateTime.now()) && encher.getStatut() != StatutEncher.TERMINEE) {
            encher.setStatut(StatutEncher.TERMINEE);

            // Déterminer le gagnant (participation avec le plus grand montant)
            Participation gagnantParticipation = encher.getParticipations().stream()
                    .max((p1, p2) -> Double.compare(p1.getMontant(), p2.getMontant()))
                    .orElse(null);

            if (gagnantParticipation != null) {
                encher.setGagnant(gagnantParticipation.getUtilisateur());
            }

            encherRepository.save(encher);
        }

        return toDto(encher);
    }


}

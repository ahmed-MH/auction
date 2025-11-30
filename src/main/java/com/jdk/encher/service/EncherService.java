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
                .categorieId(encher.getCategorie() != null ? encher.getCategorie().getId() : null)
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
    public EncherResponseDTO updateEnchere(Long id, EncherUpdateDTO dto) {

        Encher existing = encherRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Enchère introuvable ID : " + id));

        existing.setNomProduit(dto.getNomProduit());
        existing.setDescription(dto.getDescription());
        existing.setDateDebut(dto.getDateDebut());
        existing.setDateFin(dto.getDateFin());
        existing.setPrixDepart(dto.getPrixDepart());
        existing.setMontantActuel(dto.getMontantActuel());
        existing.setStatut(dto.getStatut());

        if (dto.getCategorieId() != null) {
            Categorie categorie = categorieRepository.findById(dto.getCategorieId())
                    .orElseThrow(() -> new EntityNotFoundException("Catégorie introuvable"));
            existing.setCategorie(categorie);
        }

        if (dto.getGagnantId() != null) {
            Utilisateur gagnant = utilisateurRepository.findById(dto.getGagnantId())
                    .orElseThrow(() -> new EntityNotFoundException("Gagnant introuvable"));
            existing.setGagnant(gagnant);
        }

        Encher saved = encherRepository.save(existing);
        return toDto(saved);
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
}

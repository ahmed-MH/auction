package com.jdk.encher.service;

import com.jdk.encher.entity.Encher;
import com.jdk.encher.entity.Image;
import com.jdk.encher.repository.EncherRepository;
import com.jdk.encher.repository.ImageRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EncherService {

    private final EncherRepository encherRepository;
    private final ImageRepository imageRepository;

    /**
     * Récupérer toutes les enchères
     */
    @Transactional(readOnly = true)
    public List<Encher> getAllEncheres() {
        return encherRepository.findAll();
    }

    /**
     * Récupérer une enchère par ID
     */
    @Transactional(readOnly = true)
    public Encher getEnchereById(Long id) {
        return encherRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Enchère introuvable avec ID : " + id));
    }

    /**
     * Créer une nouvelle enchère (avec ou sans images)
     */
    public Encher createEnchere(Encher encher) {
        Encher savedEncher = encherRepository.save(encher);

        if (encher.getImages() != null && !encher.getImages().isEmpty()) {
            encher.getImages().forEach(image -> {
                image.setEncher(savedEncher);
                imageRepository.save(image);
            });
        }

        return savedEncher;
    }

    /**
     * Mettre à jour une enchère
     */
    public Encher updateEnchere(Long id, Encher updatedEncher) {
        Encher existing = encherRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Enchère introuvable avec ID : " + id));

        existing.setNomProduit(updatedEncher.getNomProduit());
        existing.setDescription(updatedEncher.getDescription());
        existing.setDateDebut(updatedEncher.getDateDebut());
        existing.setDateFin(updatedEncher.getDateFin());
        existing.setPrixDepart(updatedEncher.getPrixDepart());
        existing.setMontantActuel(updatedEncher.getMontantActuel());
        existing.setStatut(updatedEncher.getStatut());
        existing.setCreateur(updatedEncher.getCreateur());
        existing.setGagnant(updatedEncher.getGagnant());

        // Gestion des images
        if (updatedEncher.getImages() != null) {
            imageRepository.deleteAll(existing.getImages());

            updatedEncher.getImages().forEach(image -> {
                image.setEncher(existing);
                imageRepository.save(image);
            });
        }

        return encherRepository.save(existing);
    }

    /**
     * Supprimer une enchère
     */
    public void deleteEnchere(Long id) {
        Encher existing = encherRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Enchère introuvable avec ID : " + id));

        // Supprime d'abord les images associées
        imageRepository.deleteAll(existing.getImages());
        encherRepository.delete(existing);
    }

    /**
     * Ajouter une image à une enchère existante
     */
    public Image addImageToEnchere(Long encherId, Image image) {
        Encher encher = encherRepository.findById(encherId)
                .orElseThrow(() -> new EntityNotFoundException("Enchère introuvable avec ID : " + encherId));

        image.setEncher(encher);
        return imageRepository.save(image);
    }

    /**
     * Lister toutes les images d’une enchère
     */
    @Transactional(readOnly = true)
    public List<Image> getImagesByEnchere(Long encherId) {
        Encher encher = encherRepository.findById(encherId)
                .orElseThrow(() -> new EntityNotFoundException("Enchère introuvable avec ID : " + encherId));

        return encher.getImages();
    }
}

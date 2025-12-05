package com.jdk.encher.service;

import com.jdk.encher.entity.Enchere;
import com.jdk.encher.entity.Image;
import com.jdk.encher.repository.EnchereRepository;
import com.jdk.encher.repository.ImageRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ImageService {

    private final ImageRepository imageRepository;
    private final EnchereRepository enchereRepository;

    /**
     * Récupérer toutes les images associées à une enchère donnée
     */
    @Transactional(readOnly = true)
    public List<Image> getImagesByEnchere(Long enchereId) {
        Enchere enchere = enchereRepository.findById(enchereId)
                .orElseThrow(() -> new EntityNotFoundException("Enchère introuvable avec ID : " + enchereId));

        return imageRepository.findByEnchere(enchere);
    }

    /**
     * Ajouter une image à une enchère
     */
    public Image addImageToEnchere(Long enchereId, Image image) {
        Enchere enchere = enchereRepository.findById(enchereId)
                .orElseThrow(() -> new EntityNotFoundException("Enchère introuvable avec ID : " + enchereId));

        image.setEnchere(enchere);
        return imageRepository.save(image);
    }

    /**
     * Supprimer une image par son ID
     */
    public void deleteImage(Long id) {
        Image image = imageRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Image introuvable avec ID : " + id));

        imageRepository.delete(image);
    }

    /**
     * Récupérer une image par son ID
     */
    @Transactional(readOnly = true)
    public Image getImageById(Long id) {
        return imageRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Image introuvable avec ID : " + id));
    }
}

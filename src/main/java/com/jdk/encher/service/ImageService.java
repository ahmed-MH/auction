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
public class ImageService {

    private final ImageRepository imageRepository;
    private final EncherRepository encherRepository;

    /**
     * Récupérer toutes les images associées à une enchère donnée
     */
    @Transactional(readOnly = true)
    public List<Image> getImagesByEncher(Long encherId) {
        Encher encher = encherRepository.findById(encherId)
                .orElseThrow(() -> new EntityNotFoundException("Enchère introuvable avec ID : " + encherId));

        return imageRepository.findByEncher(encher);
    }

    /**
     * Ajouter une image à une enchère
     */
    public Image addImageToEncher(Long encherId, Image image) {
        Encher encher = encherRepository.findById(encherId)
                .orElseThrow(() -> new EntityNotFoundException("Enchère introuvable avec ID : " + encherId));

        image.setEncher(encher);
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

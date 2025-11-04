package com.jdk.encher.service;

import com.jdk.encher.entity.Encher;
import com.jdk.encher.entity.Image;
import com.jdk.encher.repository.EncherRepository;
import com.jdk.encher.repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final ImageRepository imageRepository;
    private final EncherRepository encherRepository;

    public List<Image> getImagesByEncher(Long encherId) {
        return imageRepository.findByEncherId(encherId);
    }

    public Image addImageToEncher(Long encherId, Image image) {
        Optional<Encher> encherOpt = encherRepository.findById(encherId);
        if (encherOpt.isPresent()) {
            Encher encher = encherOpt.get();
            image.setEncher(encher);
            return imageRepository.save(image);
        } else {
            throw new RuntimeException("Enchère introuvable avec l'ID : " + encherId);
        }
    }

    public void deleteImage(Long imageId) {
        imageRepository.deleteById(imageId);
    }
}

package com.jdk.encher.controller;

import com.jdk.encher.entity.Encher;
import com.jdk.encher.entity.Image;
import com.jdk.encher.repository.EncherRepository;
import com.jdk.encher.repository.ImageRepository;
import com.jdk.encher.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/encheres")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class EncherController {

    private final EncherRepository encherRepository;
    private final ImageService imageService;
    private final ImageRepository imageRepository;

    // 🔹 1. Récupérer toutes les enchères
    @GetMapping
    public ResponseEntity<List<Encher>> getAllEncheres() {
        List<Encher> encheres = encherRepository.findAll();
        return ResponseEntity.ok(encheres);
    }

    // 🔹 2. Récupérer une enchère par ID
    @GetMapping("/{id}")
    public ResponseEntity<Encher> getEnchereById(@PathVariable Long id) {
        Optional<Encher> encher = encherRepository.findById(id);
        return encher.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 🔹 3. Créer une nouvelle enchère (avec ou sans images)
    @PostMapping
    public ResponseEntity<Encher> createEnchere(@RequestBody Encher encher) {
        Encher savedEncher = encherRepository.save(encher);

        // Si des images sont incluses dans le JSON, les associer à l’enchère
        if (encher.getImages() != null && !encher.getImages().isEmpty()) {
            for (Image image : encher.getImages()) {
                image.setEncher(savedEncher);
                imageRepository.save(image);
            }
        }

        return ResponseEntity.ok(savedEncher);
    }

    // 🔹 4. Mettre à jour une enchère existante
    @PutMapping("/{id}")
    public ResponseEntity<Encher> updateEnchere(@PathVariable Long id, @RequestBody Encher updatedEncher) {
        Optional<Encher> encherOpt = encherRepository.findById(id);

        if (encherOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Encher encher = encherOpt.get();
        encher.setNomProduit(updatedEncher.getNomProduit());
        encher.setDescription(updatedEncher.getDescription());
        encher.setDateDebut(updatedEncher.getDateDebut());
        encher.setDateFin(updatedEncher.getDateFin());
        encher.setPrixDepart(updatedEncher.getPrixDepart());
        encher.setMontantActuel(updatedEncher.getMontantActuel());
        encher.setStatut(updatedEncher.getStatut());
        encher.setCreateur(updatedEncher.getCreateur());
        encher.setGagnant(updatedEncher.getGagnant());

        // ⚙️ Met à jour les images associées
        if (updatedEncher.getImages() != null) {
            // Supprime les anciennes images
            imageRepository.deleteAll(encher.getImages());

            // Ajoute les nouvelles
            for (Image image : updatedEncher.getImages()) {
                image.setEncher(encher);
                imageRepository.save(image);
            }
        }

        Encher saved = encherRepository.save(encher);
        return ResponseEntity.ok(saved);
    }

    // 🔹 5. Supprimer une enchère
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEnchere(@PathVariable Long id) {
        Optional<Encher> encherOpt = encherRepository.findById(id);
        if (encherOpt.isPresent()) {
            encherRepository.delete(encherOpt.get());
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // 🔹 6. Ajouter une image à une enchère existante
    @PostMapping("/{encherId}/images")
    public ResponseEntity<Image> addImageToEnchere(@PathVariable Long encherId, @RequestBody Image image) {
        Image savedImage = imageService.addImageToEncher(encherId, image);
        return ResponseEntity.ok(savedImage);
    }

    // 🔹 7. Lister les images d’une enchère
    @GetMapping("/{encherId}/images")
    public ResponseEntity<List<Image>> getImagesByEnchere(@PathVariable Long encherId) {
        List<Image> images = imageService.getImagesByEncher(encherId);
        return ResponseEntity.ok(images);
    }
}

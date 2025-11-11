package com.jdk.encher.controller;

import com.jdk.encher.entity.Image;
import com.jdk.encher.service.ImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Images", description = "Gestion des images des enchères")
@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ImageController {

    private final ImageService imageService;

    @Operation(summary = "Récupérer une image par ID")
    @GetMapping("/{id}")
    public ResponseEntity<Image> getImageById(@PathVariable Long id) {
        return ResponseEntity.ok(imageService.getImageById(id));
    }

    @Operation(summary = "Lister les images d’une enchère")
    @GetMapping("/encher/{encherId}")
    public ResponseEntity<List<Image>> getImagesByEncher(@PathVariable Long encherId) {
        return ResponseEntity.ok(imageService.getImagesByEncher(encherId));
    }

    @Operation(summary = "Ajouter une image à une enchère")
    @PostMapping("/encher/{encherId}")
    public ResponseEntity<Image> addImageToEncher(@PathVariable Long encherId, @Valid @RequestBody Image image) {
        return ResponseEntity.ok(imageService.addImageToEncher(encherId, image));
    }

    @Operation(summary = "Supprimer une image")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteImage(@PathVariable Long id) {
        imageService.deleteImage(id);
        return ResponseEntity.noContent().build();
    }
}

package com.jdk.encher.controller;

import com.jdk.encher.entity.Encher;
import com.jdk.encher.entity.Image;
import com.jdk.encher.service.EncherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Enchères", description = "Gestion des enchères et de leurs images")
@RestController
@RequestMapping("/api/enchers")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class EncherController {

    private final EncherService encherService;

    @Operation(summary = "Récupérer toutes les enchères")
    @GetMapping
    public ResponseEntity<List<Encher>> getAllEncheres() {
        return ResponseEntity.ok(encherService.getAllEncheres());
    }

    @Operation(summary = "Récupérer une enchère par ID")
    @GetMapping("/{id}")
    public ResponseEntity<Encher> getEnchereById(@PathVariable Long id) {
        return ResponseEntity.ok(encherService.getEnchereById(id));
    }

    @Operation(summary = "Créer une nouvelle enchère")
    @PostMapping
    public ResponseEntity<Encher> createEnchere(@Valid @RequestBody Encher encher) {
        return ResponseEntity.ok(encherService.createEnchere(encher));
    }

    @Operation(summary = "Mettre à jour une enchère existante")
    @PutMapping("/{id}")
    public ResponseEntity<Encher> updateEnchere(@PathVariable Long id, @Valid @RequestBody Encher updatedEncher) {
        return ResponseEntity.ok(encherService.updateEnchere(id, updatedEncher));
    }

    @Operation(summary = "Supprimer une enchère")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEnchere(@PathVariable Long id) {
        encherService.deleteEnchere(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Ajouter une image à une enchère")
    @PostMapping("/{encherId}/images")
    public ResponseEntity<Image> addImageToEnchere(@PathVariable Long encherId, @Valid @RequestBody Image image) {
        return ResponseEntity.ok(encherService.addImageToEnchere(encherId, image));
    }

    @Operation(summary = "Lister les images d’une enchère")
    @GetMapping("/{encherId}/images")
    public ResponseEntity<List<Image>> getImagesByEnchere(@PathVariable Long encherId) {
        return ResponseEntity.ok(encherService.getImagesByEnchere(encherId));
    }
}

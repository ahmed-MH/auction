package com.jdk.encher.controller;

import com.jdk.encher.dto.EncherCreateDTO;
import com.jdk.encher.dto.EncherResponseDTO;
import com.jdk.encher.dto.EncherUpdateDTO;
import com.jdk.encher.entity.Encher;
import com.jdk.encher.entity.Image;
import com.jdk.encher.service.EncherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/enchers")
@CrossOrigin("*")
public class EnchereController {

    private final EncherService encherService;

    // -----------------------------------------------------
    // 🔹 GET ALL ENCHERES
    // -----------------------------------------------------
    @GetMapping
    public ResponseEntity<List<EncherResponseDTO>> getAll() {
        System.out.println(encherService.getAllEncheres());
        return ResponseEntity.ok(encherService.getAllEncheres());
    }

    // -----------------------------------------------------
    // 🔹 GET ENCHERE BY ID
    // -----------------------------------------------------
    @GetMapping("/{id}")
    public ResponseEntity<EncherResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(encherService.checkAndCloseEnchere(id));
    }


    // -----------------------------------------------------
    // 🔹 CREATE ENCHERE
    // -----------------------------------------------------
    @PostMapping
    public ResponseEntity<EncherResponseDTO> create(@RequestBody EncherCreateDTO dto) {
        return ResponseEntity.ok(encherService.createEnchere(dto));
    }

    // -----------------------------------------------------
    // 🔹 UPDATE ENCHERE
    // -----------------------------------------------------
    @PutMapping("/{id}")
    public ResponseEntity<EncherResponseDTO> update(
            @PathVariable Long id,
            @RequestBody EncherUpdateDTO dto
    ) {
        // Convert to DTO
        return ResponseEntity.ok(encherService.updateEnchereDTO(id, dto));
    }

    // -----------------------------------------------------
    // 🔹 DELETE ENCHERE
    // -----------------------------------------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        encherService.deleteEnchere(id);
        return ResponseEntity.noContent().build();
    }

    // -----------------------------------------------------
    // 🔹 GET IMAGES OF AN ENCHERE
    // -----------------------------------------------------
    @GetMapping("/{id}/images")
    public ResponseEntity<List<String>> getImages(@PathVariable Long id) {
        return ResponseEntity.ok(encherService.getImagesByEnchere(id));
    }
    // -----------------------------------------------------
    // 🔹 GET ENCHERES BY CATEGORIE
    // -----------------------------------------------------
    @GetMapping("/categorie/{categorieId}")
    public ResponseEntity<List<EncherResponseDTO>> getByCategorie(@PathVariable Long categorieId) {
        return ResponseEntity.ok(encherService.getEncheresByCategorie(categorieId));
    }

    @GetMapping("/utilisateur/{id}")
    public ResponseEntity<List<EncherResponseDTO>> getByUtilisateur(@PathVariable Long id) {
        return ResponseEntity.ok(encherService.getEnchersByUtilisateur(id));
    }

}

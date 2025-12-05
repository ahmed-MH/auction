package com.jdk.encher.controller;

import com.jdk.encher.dto.EnchereCreateDTO;
import com.jdk.encher.dto.EnchereResponseDTO;
import com.jdk.encher.dto.EnchereUpdateDTO;
import com.jdk.encher.service.EnchereService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/encheres")
@CrossOrigin("*")
public class EnchereController {

    private final EnchereService enchereService;

    // -----------------------------------------------------
    // 🔹 GET ALL ENCHERES
    // -----------------------------------------------------
    @GetMapping
    public ResponseEntity<List<EnchereResponseDTO>> getAll() {
        System.out.println(enchereService.getAllEncheres());
        return ResponseEntity.ok(enchereService.getAllEncheres());
    }

    // -----------------------------------------------------
    // 🔹 GET ENCHERE BY ID
    // -----------------------------------------------------
    @GetMapping("/{id}")
    public ResponseEntity<EnchereResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(enchereService.checkAndCloseEnchere(id));
    }

    // -----------------------------------------------------
    // 🔹 CREATE ENCHERE
    // -----------------------------------------------------
    @PostMapping
    public ResponseEntity<EnchereResponseDTO> create(@RequestBody EnchereCreateDTO dto) {
        return ResponseEntity.ok(enchereService.createEnchere(dto));
    }

    // -----------------------------------------------------
    // 🔹 UPDATE ENCHERE
    // -----------------------------------------------------
    @PutMapping("/{id}")
    public ResponseEntity<EnchereResponseDTO> update(
            @PathVariable Long id,
            @RequestBody EnchereUpdateDTO dto) {
        // Convert to DTO
        return ResponseEntity.ok(enchereService.updateEnchereDTO(id, dto));
    }

    // -----------------------------------------------------
    // 🔹 DELETE ENCHERE
    // -----------------------------------------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        enchereService.deleteEnchere(id);
        return ResponseEntity.noContent().build();
    }

    // -----------------------------------------------------
    // 🔹 GET IMAGES OF AN ENCHERE
    // -----------------------------------------------------
    @GetMapping("/{id}/images")
    public ResponseEntity<List<String>> getImages(@PathVariable Long id) {
        return ResponseEntity.ok(enchereService.getImagesByEnchere(id));
    }

    // -----------------------------------------------------
    // 🔹 GET ENCHERES BY CATEGORIE
    // -----------------------------------------------------
    @GetMapping("/categorie/{categorieId}")
    public ResponseEntity<List<EnchereResponseDTO>> getByCategorie(@PathVariable Long categorieId) {
        return ResponseEntity.ok(enchereService.getEncheresByCategorie(categorieId));
    }

    @GetMapping("/utilisateur/{id}")
    public ResponseEntity<List<EnchereResponseDTO>> getByUtilisateur(@PathVariable Long id) {
        return ResponseEntity.ok(enchereService.getEnchersByUtilisateur(id));
    }

}

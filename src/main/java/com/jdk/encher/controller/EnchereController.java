package com.jdk.encher.controller;

import com.jdk.encher.dto.EncherCreateDTO;
import com.jdk.encher.dto.EncherResponseDTO;
import com.jdk.encher.dto.EncherUpdateDTO;
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
@RequestMapping("/api/encheres")
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
        return ResponseEntity.ok(encherService.getEnchereById(id));
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
        return ResponseEntity.ok(encherService.updateEnchere(id, dto));
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

}

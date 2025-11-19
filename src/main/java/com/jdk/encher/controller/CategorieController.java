package com.jdk.encher.controller;

import com.jdk.encher.dto.CategorieDTO;
import com.jdk.encher.service.CategorieService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategorieController {

    private final CategorieService categorieService;

    @PostMapping
    public ResponseEntity<CategorieDTO> create(@RequestBody CategorieDTO dto) {
        return ResponseEntity.ok(categorieService.createCategorie(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategorieDTO> update(@PathVariable Long id, @RequestBody CategorieDTO dto) {
        return ResponseEntity.ok(categorieService.updateCategorie(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        categorieService.deleteCategorie(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategorieDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(categorieService.getCategorie(id));
    }

    @GetMapping
    public ResponseEntity<List<CategorieDTO>> getAll() {
        return ResponseEntity.ok(categorieService.getAllCategories());
    }
}

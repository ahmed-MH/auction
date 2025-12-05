package com.jdk.encher.controller;

import com.jdk.encher.dto.CategorieDTO;
import com.jdk.encher.service.CategorieService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategorieController {

    private final CategorieService categorieService;

    @PostMapping
    public ResponseEntity<?> create(@RequestBody CategorieDTO dto) {
        try {
            log.info("🔵 POST /api/categories - Création: {}", dto.getLibelleCategorie());
            CategorieDTO created = categorieService.createCategorie(dto);
            log.info("✅ Catégorie créée: {}", created);
            return ResponseEntity.ok(created);
        } catch (DataIntegrityViolationException e) {
            log.error("❌ Contrainte violée: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("message", "Cette catégorie existe déjà !");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
        } catch (RuntimeException e) {
            log.error("❌ Erreur création: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            log.error("❌ Erreur inattendue: ", e);
            Map<String, String> error = new HashMap<>();
            error.put("message", "Erreur serveur: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody CategorieDTO dto) {
        try {
            log.info("🔵 PUT /api/categories/{} - Modification: {}", id, dto.getLibelleCategorie());
            CategorieDTO updated = categorieService.updateCategorie(id, dto);
            log.info("✅ Catégorie modifiée: {}", updated);
            return ResponseEntity.ok(updated);
        } catch (DataIntegrityViolationException e) {
            log.error("❌ Contrainte violée: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("message", "Une catégorie avec ce nom existe déjà !");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
        } catch (RuntimeException e) {
            log.error("❌ Erreur modification: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            log.error("❌ Erreur inattendue: ", e);
            Map<String, String> error = new HashMap<>();
            error.put("message", "Erreur serveur: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            log.info("🔵 DELETE /api/categories/{}", id);
            categorieService.deleteCategorie(id);
            log.info("✅ Catégorie supprimée");
            return ResponseEntity.noContent().build();
        } catch (DataIntegrityViolationException e) {
            log.error("❌ Contrainte violée: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("message", "Impossible de supprimer : cette catégorie est utilisée");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
        } catch (RuntimeException e) {
            log.error("❌ Erreur suppression: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            log.error("❌ Erreur inattendue: ", e);
            Map<String, String> error = new HashMap<>();
            error.put("message", "Erreur serveur: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable Long id) {
        try {
            log.info("🔵 GET /api/categories/{}", id);
            CategorieDTO categorie = categorieService.getCategorie(id);
            return ResponseEntity.ok(categorie);
        } catch (RuntimeException e) {
            log.error("❌ Erreur: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    @GetMapping
    public ResponseEntity<List<CategorieDTO>> getAll() {
        log.info("🔵 GET /api/categories");
        List<CategorieDTO> categories = categorieService.getAllCategories();
        log.info("✅ {} catégories récupérées", categories.size());
        return ResponseEntity.ok(categories);
    }
}
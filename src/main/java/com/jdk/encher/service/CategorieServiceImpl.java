package com.jdk.encher.service;

import com.jdk.encher.dto.CategorieDTO;
import com.jdk.encher.entity.Categorie;
import com.jdk.encher.repository.CategorieRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategorieServiceImpl implements CategorieService {

    private final CategorieRepository categorieRepository;

    @Override
    @Transactional
    public CategorieDTO createCategorie(CategorieDTO dto) {
        log.info("📝 Création catégorie: {}", dto.getLibelleCategorie());

        // Vérifier si la catégorie existe déjà
        if (categorieRepository.existsByLibelleCategorie(dto.getLibelleCategorie())) {
            log.warn("⚠️ Catégorie déjà existante: {}", dto.getLibelleCategorie());
            throw new RuntimeException("Cette catégorie existe déjà !");
        }

        Categorie categorie = Categorie.builder()
                .libelleCategorie(dto.getLibelleCategorie())
                .build();

        Categorie saved = categorieRepository.save(categorie);
        log.info("✅ Catégorie créée avec succès: ID={}", saved.getId());

        return toDTO(saved);
    }

    @Override
    @Transactional
    public CategorieDTO updateCategorie(Long id, CategorieDTO dto) {
        log.info("📝 Modification catégorie ID={}: nouveau nom={}", id, dto.getLibelleCategorie());

        Categorie categorie = categorieRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("❌ Catégorie introuvable: ID={}", id);
                    return new RuntimeException("Catégorie introuvable !");
                });

        log.info("📋 Ancien nom: {}", categorie.getLibelleCategorie());

        // Si le nom change, vérifier qu'il n'existe pas déjà
        if (!categorie.getLibelleCategorie().equalsIgnoreCase(dto.getLibelleCategorie())) {
            if (categorieRepository.existsByLibelleCategorie(dto.getLibelleCategorie())) {
                log.warn("⚠️ Nom déjà utilisé: {}", dto.getLibelleCategorie());
                throw new RuntimeException("Une catégorie avec ce nom existe déjà !");
            }
        }

        categorie.setLibelleCategorie(dto.getLibelleCategorie());

        Categorie updated = categorieRepository.save(categorie);
        log.info("✅ Catégorie modifiée avec succès");

        return toDTO(updated);
    }

    @Override
    @Transactional
    public void deleteCategorie(Long id) {
        log.info("🗑️ Suppression catégorie ID={}", id);

        if (!categorieRepository.existsById(id)) {
            log.error("❌ Catégorie introuvable: ID={}", id);
            throw new RuntimeException("Catégorie introuvable !");
        }

        categorieRepository.deleteById(id);
        log.info("✅ Catégorie supprimée avec succès");
    }

    @Override
    @Transactional(readOnly = true)
    public CategorieDTO getCategorie(Long id) {
        log.info("🔍 Recherche catégorie ID={}", id);

        return categorieRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> {
                    log.error("❌ Catégorie introuvable: ID={}", id);
                    return new RuntimeException("Catégorie introuvable !");
                });
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategorieDTO> getAllCategories() {
        log.info("📋 Récupération de toutes les catégories");

        List<CategorieDTO> categories = categorieRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());

        log.info("✅ {} catégorie(s) trouvée(s)", categories.size());
        return categories;
    }

    private CategorieDTO toDTO(Categorie categorie) {
        return new CategorieDTO(categorie.getId(), categorie.getLibelleCategorie());
    }
}
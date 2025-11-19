package com.jdk.encher.service;


import com.jdk.encher.dto.CategorieDTO;
import com.jdk.encher.entity.Categorie;
import com.jdk.encher.repository.CategorieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategorieServiceImpl implements CategorieService {

    private final CategorieRepository categorieRepository;

    @Override
    public CategorieDTO createCategorie(CategorieDTO dto) {
        if (categorieRepository.existsByLibelleCategorie(dto.getLibelleCategorie())) {
            throw new RuntimeException("La catégorie existe déjà !");
        }

        Categorie categorie = Categorie.builder()
                .libelleCategorie(dto.getLibelleCategorie())
                .build();

        return toDTO(categorieRepository.save(categorie));
    }

    @Override
    public CategorieDTO updateCategorie(Long id, CategorieDTO dto) {
        Categorie categorie = categorieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Catégorie introuvable !"));

        categorie.setLibelleCategorie(dto.getLibelleCategorie());

        return toDTO(categorieRepository.save(categorie));
    }

    @Override
    public void deleteCategorie(Long id) {
        categorieRepository.deleteById(id);
    }

    @Override
    public CategorieDTO getCategorie(Long id) {
        return categorieRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new RuntimeException("Catégorie introuvable !"));
    }

    @Override
    public List<CategorieDTO> getAllCategories() {
        return categorieRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    private CategorieDTO toDTO(Categorie categorie) {
        return new CategorieDTO(categorie.getId(), categorie.getLibelleCategorie());
    }
}

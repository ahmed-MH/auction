package com.jdk.encher.service;

import com.jdk.encher.dto.CategorieDTO;

import java.util.List;

public interface CategorieService {
    CategorieDTO createCategorie(CategorieDTO dto);
    CategorieDTO updateCategorie(Long id, CategorieDTO dto);
    void deleteCategorie(Long id);
    CategorieDTO getCategorie(Long id);
    List<CategorieDTO> getAllCategories();
}


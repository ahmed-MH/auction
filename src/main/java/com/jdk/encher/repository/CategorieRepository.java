package com.jdk.encher.repository;

import com.jdk.encher.entity.Categorie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategorieRepository extends JpaRepository<Categorie, Long> {
    boolean existsByLibelleCategorie(String libelleCategorie);
}


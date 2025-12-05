package com.jdk.encher.repository;

import com.jdk.encher.entity.Categorie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategorieRepository extends JpaRepository<Categorie, Long> {
    boolean existsByLibelleCategorie(String libelleCategorie);
    Optional<Categorie> findByLibelleCategorie(String libelleCategorie);
}
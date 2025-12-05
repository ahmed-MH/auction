package com.jdk.encher.repository;

import com.jdk.encher.entity.Enchere;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EnchereRepository extends JpaRepository<Enchere, Long> {
    List<Enchere> findByCategorieId(Long categorieId);

    List<Enchere> findByCreateurId(Long createurId);

    boolean existsByCreateurId(Long id);

}

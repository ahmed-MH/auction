package com.jdk.encher.repository;

import com.jdk.encher.entity.Encher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EncherRepository extends JpaRepository<Encher, Long> {
    List<Encher> findByCategorieId(Long categorieId);
    List<Encher> findByCreateurId(Long createurId);
    boolean existsByCreateurId(Long id);

}

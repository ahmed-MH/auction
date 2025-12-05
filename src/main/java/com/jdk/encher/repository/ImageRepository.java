package com.jdk.encher.repository;

import com.jdk.encher.entity.Enchere;
import com.jdk.encher.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {
    List<Image> findByEnchere(Enchere enchere);

}

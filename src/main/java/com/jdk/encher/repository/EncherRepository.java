package com.jdk.encher.repository;

import com.jdk.encher.entity.Encher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EncherRepository extends JpaRepository<Encher, Long> {
}

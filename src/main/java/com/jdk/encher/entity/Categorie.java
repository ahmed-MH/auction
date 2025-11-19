package com.jdk.encher.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "categorie")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Categorie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le libellé de la catégorie ne peut pas être vide")
    @Column(name = "libelle_categorie", unique = true, nullable = false)
    private String libelleCategorie;

    // Relation optionnelle vers les enchères
    @OneToMany(mappedBy = "categorie", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Encher> encheres;

    @Override
    public String toString() {
        return "Categorie{" +
                "id=" + id +
                ", libelleCategorie='" + libelleCategorie + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Categorie)) return false;
        return id != null && id.equals(((Categorie) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}

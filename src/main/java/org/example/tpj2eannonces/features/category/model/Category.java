package org.example.tpj2eannonces.features.category.model;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

import org.example.tpj2eannonces.features.annonce.model.Annonce;
import org.example.tpj2eannonces.shared.model.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "category")
public class Category extends BaseEntity<Long> {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le libellé est obligatoire")
    @Size(max = 50, message = "Le libellé ne doit pas dépasser 50 caractères")
    @Column(unique = true, nullable = false, length = 50)
    private String label;

    @OneToMany(mappedBy = "category")
    private List<Annonce> annonces = new ArrayList<>();

    public Category() {
    }

    public Category(String label) {
        this.label = label;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public List<Annonce> getAnnonces() {
        return annonces;
    }

    public void setAnnonces(List<Annonce> annonces) {
        this.annonces = annonces;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Category category)) {
            return false;
        }
        return super.equals(category);
    }

    @Override
    public int hashCode() {
        return 31 * super.hashCode();
    }

    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", label='" + label + '\'' +
                '}';
    }
}

package org.example.tpj2eannonces.features.category.repository;

import org.example.tpj2eannonces.features.category.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
}

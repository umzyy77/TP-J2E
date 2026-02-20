package org.example.tpj2eannonces.features.annonce.repository;

import java.util.Optional;

import org.example.tpj2eannonces.features.annonce.model.Annonce;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface AnnonceRepository extends JpaRepository<Annonce, Long>, JpaSpecificationExecutor<Annonce> {

    @EntityGraph(attributePaths = {"author", "category"})
    Optional<Annonce> findWithRelationsById(Long id);

    @EntityGraph(attributePaths = {"author", "category"})
    Page<Annonce> findAll(Pageable pageable);
}

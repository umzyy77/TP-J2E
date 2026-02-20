package org.example.tpj2eannonces.features.annonce.repository;

import java.util.List;
import java.util.Optional;

import org.example.tpj2eannonces.features.annonce.model.Annonce;
import org.example.tpj2eannonces.features.annonce.model.AnnonceStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AnnonceRepository extends JpaRepository<Annonce, Long>, JpaSpecificationExecutor<Annonce> {

    @EntityGraph(attributePaths = {"author", "category"})
    Optional<Annonce> findWithRelationsById(Long id);

    @EntityGraph(attributePaths = {"author", "category"})
    Page<Annonce> findAll(Pageable pageable);

    @Query("SELECT a FROM Annonce a JOIN FETCH a.author JOIN FETCH a.category " +
           "WHERE LOWER(a.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(a.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Annonce> searchByKeyword(@Param("keyword") String keyword);

    @Query("SELECT COUNT(a) FROM Annonce a WHERE a.status = :status")
    long countByStatus(@Param("status") AnnonceStatus status);
}

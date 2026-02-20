package org.example.tpj2eannonces.features.annonce.repository;

import java.time.LocalDateTime;
import java.util.UUID;

import org.example.tpj2eannonces.features.annonce.model.Annonce;
import org.example.tpj2eannonces.features.annonce.model.AnnonceStatus;
import org.springframework.data.jpa.domain.Specification;

public final class AnnonceSpecifications {

    private AnnonceSpecifications() {
    }

    public static Specification<Annonce> hasKeyword(String keyword) {
        return (root, query, cb) -> {
            String pattern = "%" + keyword.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("title")), pattern),
                    cb.like(cb.lower(root.get("description")), pattern)
            );
        };
    }

    public static Specification<Annonce> hasStatus(AnnonceStatus status) {
        return (root, query, cb) -> cb.equal(root.get("status"), status);
    }

    public static Specification<Annonce> hasCategoryId(Long categoryId) {
        return (root, query, cb) -> cb.equal(root.get("category").get("id"), categoryId);
    }

    public static Specification<Annonce> hasAuthorId(UUID authorId) {
        return (root, query, cb) -> cb.equal(root.get("author").get("id"), authorId);
    }

    public static Specification<Annonce> createdAfter(LocalDateTime fromDate) {
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("date"), fromDate);
    }

    public static Specification<Annonce> createdBefore(LocalDateTime toDate) {
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("date"), toDate);
    }
}

package org.example.tpj2eannonces.features.annonce.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.example.tpj2eannonces.features.annonce.dto.AnnonceFormDTO;
import org.example.tpj2eannonces.features.annonce.dto.AnnonceResponseDTO;
import org.example.tpj2eannonces.features.annonce.mapper.AnnonceMapper;
import org.example.tpj2eannonces.features.annonce.model.Annonce;
import org.example.tpj2eannonces.features.annonce.model.AnnonceStatus;
import org.example.tpj2eannonces.features.annonce.repository.AnnonceRepository;
import org.example.tpj2eannonces.features.annonce.repository.AnnonceSpecifications;
import org.example.tpj2eannonces.features.category.model.Category;
import org.example.tpj2eannonces.features.category.repository.CategoryRepository;
import org.example.tpj2eannonces.features.user.model.User;
import org.example.tpj2eannonces.features.user.repository.UserRepository;
import org.example.tpj2eannonces.shared.exception.ForbiddenException;
import org.example.tpj2eannonces.shared.exception.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AnnonceService {

    private final AnnonceRepository annonceRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final AnnonceMapper annonceMapper;

    public AnnonceService(AnnonceRepository annonceRepository,
                          UserRepository userRepository,
                          CategoryRepository categoryRepository,
                          AnnonceMapper annonceMapper) {
        this.annonceRepository = annonceRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.annonceMapper = annonceMapper;
    }

    public AnnonceResponseDTO findById(Long id) {
        Annonce annonce = annonceRepository.findWithRelationsById(id)
                .orElseThrow(() -> new NotFoundException("Annonce non trouvee: " + id));
        return annonceMapper.toResponseDTO(annonce);
    }

    public Page<AnnonceResponseDTO> findAll(Pageable pageable) {
        return annonceRepository.findAll(pageable)
                .map(annonceMapper::toResponseDTO);
    }

    public Page<AnnonceResponseDTO> search(String keyword, AnnonceStatus status, Long categoryId,
                                           UUID authorId, LocalDateTime fromDate, LocalDateTime toDate,
                                           Pageable pageable) {
        Specification<Annonce> spec = Specification.where((Specification<Annonce>) null);

        if (keyword != null && !keyword.isBlank()) {
            spec = spec.and(AnnonceSpecifications.hasKeyword(keyword));
        }
        if (status != null) {
            spec = spec.and(AnnonceSpecifications.hasStatus(status));
        }
        if (categoryId != null) {
            spec = spec.and(AnnonceSpecifications.hasCategoryId(categoryId));
        }
        if (authorId != null) {
            spec = spec.and(AnnonceSpecifications.hasAuthorId(authorId));
        }
        if (fromDate != null) {
            spec = spec.and(AnnonceSpecifications.createdAfter(fromDate));
        }
        if (toDate != null) {
            spec = spec.and(AnnonceSpecifications.createdBefore(toDate));
        }

        return annonceRepository.findAll(spec, pageable)
                .map(annonceMapper::toResponseDTO);
    }

    @Transactional
    public AnnonceResponseDTO create(AnnonceFormDTO dto, UUID authorId) {
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new NotFoundException("Utilisateur non trouve: " + authorId));
        Category category = categoryRepository.findById(dto.categoryId())
                .orElseThrow(() -> new NotFoundException("Categorie non trouvee: " + dto.categoryId()));

        Annonce annonce = annonceMapper.toEntity(dto);
        annonce.setAuthor(author);
        annonce.setCategory(category);

        Annonce saved = annonceRepository.save(annonce);
        return annonceMapper.toResponseDTO(saved);
    }

    @Transactional
    public AnnonceResponseDTO update(Long id, AnnonceFormDTO dto, UUID currentUserId) {
        Annonce annonce = annonceRepository.findWithRelationsById(id)
                .orElseThrow(() -> new NotFoundException("Annonce non trouvee: " + id));

        checkOwnership(annonce, currentUserId);

        if (annonce.getStatus() == AnnonceStatus.PUBLISHED) {
            throw new ForbiddenException("Une annonce publiee ne peut pas etre modifiee");
        }

        annonceMapper.updateEntityFromDTO(dto, annonce);

        if (!annonce.getCategory().getId().equals(dto.categoryId())) {
            Category category = categoryRepository.findById(dto.categoryId())
                    .orElseThrow(() -> new NotFoundException("Categorie non trouvee: " + dto.categoryId()));
            annonce.setCategory(category);
        }

        Annonce updated = annonceRepository.save(annonce);
        return annonceMapper.toResponseDTO(updated);
    }

    @Transactional
    public void delete(Long id, UUID currentUserId) {
        Annonce annonce = annonceRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Annonce non trouvee: " + id));

        checkOwnership(annonce, currentUserId);

        if (annonce.getStatus() != AnnonceStatus.ARCHIVED) {
            throw new ForbiddenException("Seule une annonce archivee peut etre supprimee");
        }

        annonceRepository.delete(annonce);
    }

    @Transactional
    public AnnonceResponseDTO changeStatus(Long id, String action, UUID currentUserId) {
        Annonce annonce = annonceRepository.findWithRelationsById(id)
                .orElseThrow(() -> new NotFoundException("Annonce non trouvee: " + id));

        checkOwnership(annonce, currentUserId);

        AnnonceStatus expectedCurrentStatus = AnnonceStatus.fromAction(action)
                .orElseThrow(() -> new IllegalArgumentException("Action inconnue: " + action));

        if (annonce.getStatus() != expectedCurrentStatus) {
            throw new IllegalStateException(
                    "Transition invalide: impossible d'appliquer '" + action + "' sur le statut " + annonce.getStatus());
        }

        annonce.setStatus(expectedCurrentStatus.getNextStatus());
        Annonce updated = annonceRepository.save(annonce);
        return annonceMapper.toResponseDTO(updated);
    }

    private void checkOwnership(Annonce annonce, UUID currentUserId) {
        if (currentUserId == null || !currentUserId.equals(annonce.getOwnerId())) {
            throw new ForbiddenException("Vous n'etes pas l'auteur de cette annonce");
        }
    }
}

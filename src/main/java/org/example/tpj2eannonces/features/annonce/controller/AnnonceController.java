package org.example.tpj2eannonces.features.annonce.controller;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.UUID;

import org.example.tpj2eannonces.features.annonce.dto.AnnonceFormDTO;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.example.tpj2eannonces.features.annonce.dto.AnnonceResponseDTO;
import org.example.tpj2eannonces.features.annonce.dto.AnnonceStatusDTO;
import org.example.tpj2eannonces.features.annonce.model.AnnonceStatus;
import org.example.tpj2eannonces.features.annonce.service.AnnonceService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/annonces")
public class AnnonceController {

    private final AnnonceService annonceService;

    public AnnonceController(AnnonceService annonceService) {
        this.annonceService = annonceService;
    }

    @GetMapping
    public ResponseEntity<Page<AnnonceResponseDTO>> list(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) AnnonceStatus status,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) UUID authorId,
            @RequestParam(required = false) LocalDateTime fromDate,
            @RequestParam(required = false) LocalDateTime toDate,
            @PageableDefault(size = 10) Pageable pageable) {
        boolean hasFilters = q != null || status != null || categoryId != null
                || authorId != null || fromDate != null || toDate != null;

        if (hasFilters) {
            return ResponseEntity.ok(annonceService.search(q, status, categoryId, authorId, fromDate, toDate, pageable));
        }
        return ResponseEntity.ok(annonceService.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AnnonceResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(annonceService.findById(id));
    }

    @PostMapping
    public ResponseEntity<AnnonceResponseDTO> create(@Valid @RequestBody AnnonceFormDTO dto) {
        UUID currentUserId = getCurrentUserId();
        AnnonceResponseDTO created = annonceService.create(dto, currentUserId);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();

        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AnnonceResponseDTO> update(@PathVariable Long id,
                                                     @Valid @RequestBody AnnonceFormDTO dto) {
        UUID currentUserId = getCurrentUserId();
        return ResponseEntity.ok(annonceService.update(id, dto, currentUserId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        UUID currentUserId = getCurrentUserId();
        annonceService.delete(id, currentUserId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<AnnonceResponseDTO> changeStatus(@PathVariable Long id,
                                                           @Valid @RequestBody AnnonceStatusDTO dto) {
        UUID currentUserId = getCurrentUserId();
        return ResponseEntity.ok(annonceService.changeStatus(id, dto.action(), currentUserId));
    }

    private UUID getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return UUID.fromString((String) authentication.getPrincipal());
    }
}

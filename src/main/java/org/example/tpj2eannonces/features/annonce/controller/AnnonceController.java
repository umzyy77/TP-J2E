package org.example.tpj2eannonces.features.annonce.controller;

import java.net.URI;
import java.util.UUID;

import org.example.tpj2eannonces.core.security.SecurityContextFacade;
import org.example.tpj2eannonces.features.annonce.dto.AnnonceFormDTO;
import org.example.tpj2eannonces.features.annonce.dto.AnnonceResponseDTO;
import org.example.tpj2eannonces.features.annonce.dto.AnnonceSearchDTO;
import org.example.tpj2eannonces.features.annonce.dto.AnnonceStatusDTO;
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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/annonces")
public class AnnonceController {

    private final AnnonceService annonceService;
    private final SecurityContextFacade securityContextFacade;

    public AnnonceController(AnnonceService annonceService, SecurityContextFacade securityContextFacade) {
        this.annonceService = annonceService;
        this.securityContextFacade = securityContextFacade;
    }

    @GetMapping
    public ResponseEntity<Page<AnnonceResponseDTO>> list(
            @ModelAttribute AnnonceSearchDTO filters,
            @PageableDefault(size = 10) Pageable pageable) {
        if (filters.hasFilters()) {
            return ResponseEntity.ok(annonceService.search(
                    filters.q(),
                    filters.status(),
                    filters.categoryId(),
                    filters.authorId(),
                    filters.fromDate(),
                    filters.toDate(),
                    pageable));
        }
        return ResponseEntity.ok(annonceService.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AnnonceResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(annonceService.findById(id));
    }

    @PostMapping
    public ResponseEntity<AnnonceResponseDTO> create(@Valid @RequestBody AnnonceFormDTO dto) {
        UUID currentUserId = securityContextFacade.requireCurrentUserId();
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
        UUID currentUserId = securityContextFacade.requireCurrentUserId();
        return ResponseEntity.ok(annonceService.update(id, dto, currentUserId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        UUID currentUserId = securityContextFacade.requireCurrentUserId();
        annonceService.delete(id, currentUserId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<AnnonceResponseDTO> changeStatus(@PathVariable Long id,
                                                           @Valid @RequestBody AnnonceStatusDTO dto) {
        UUID currentUserId = securityContextFacade.requireCurrentUserId();
        return ResponseEntity.ok(annonceService.changeStatus(id, dto.action(), currentUserId));
    }
}

package org.example.tpj2eannonces.features.annonce.controller;

import java.net.URI;
import java.util.UUID;

import org.example.tpj2eannonces.core.security.SecurityContextFacade;
import org.example.tpj2eannonces.core.web.dto.ApiErrorDTO;
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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import org.springdoc.core.annotations.ParameterObject;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/annonces")
@Tag(name = "Annonces", description = "CRUD et gestion du cycle de vie des annonces")
public class AnnonceController {

    private final AnnonceService annonceService;
    private final SecurityContextFacade securityContextFacade;

    public AnnonceController(AnnonceService annonceService, SecurityContextFacade securityContextFacade) {
        this.annonceService = annonceService;
        this.securityContextFacade = securityContextFacade;
    }

    @GetMapping
    @Operation(summary = "Lister les annonces", description = "Retourne une page d'annonces, avec filtres optionnels")
    @ApiResponse(responseCode = "200", description = "Page d'annonces")
    public ResponseEntity<Page<AnnonceResponseDTO>> list(
            @ModelAttribute AnnonceSearchDTO filters,
            @ParameterObject @PageableDefault(size = 10) Pageable pageable) {
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
    @Operation(summary = "Obtenir une annonce", description = "Retourne une annonce par son identifiant")
    @ApiResponse(responseCode = "200", description = "Annonce trouvee")
    @ApiResponse(responseCode = "404", description = "Annonce non trouvee",
            content = @Content(schema = @Schema(implementation = ApiErrorDTO.class),
                    examples = @ExampleObject(value = "{\"error\": \"NOT_FOUND\", \"messages\": [\"Annonce non trouvee: 99\"]}")))
    public ResponseEntity<AnnonceResponseDTO> getById(
            @Parameter(description = "ID de l'annonce", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(annonceService.findById(id));
    }

    @PostMapping
    @Operation(summary = "Creer une annonce", description = "Cree une nouvelle annonce (statut DRAFT)")
    @ApiResponse(responseCode = "201", description = "Annonce creee")
    @ApiResponse(responseCode = "400", description = "Donnees invalides",
            content = @Content(schema = @Schema(implementation = ApiErrorDTO.class),
                    examples = @ExampleObject(
                            value = "{\"error\": \"VALIDATION_ERROR\", \"messages\": [\"title: Le titre est obligatoire\"]}")))
    @ApiResponse(responseCode = "401", description = "Non authentifie",
            content = @Content(schema = @Schema(implementation = ApiErrorDTO.class)))
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
    @Operation(summary = "Modifier une annonce", description = "Modifie une annonce au statut DRAFT (auteur uniquement)")
    @ApiResponse(responseCode = "200", description = "Annonce modifiee")
    @ApiResponse(responseCode = "403", description = "Non autorise (pas l'auteur ou annonce publiee)",
            content = @Content(schema = @Schema(implementation = ApiErrorDTO.class),
                    examples = @ExampleObject(
                            value = "{\"error\": \"FORBIDDEN\", \"messages\": [\"Vous n'etes pas l'auteur de cette annonce\"]}")))
    @ApiResponse(responseCode = "404", description = "Annonce non trouvee",
            content = @Content(schema = @Schema(implementation = ApiErrorDTO.class)))
    public ResponseEntity<AnnonceResponseDTO> update(
            @Parameter(description = "ID de l'annonce", example = "1") @PathVariable Long id,
            @Valid @RequestBody AnnonceFormDTO dto) {
        UUID currentUserId = securityContextFacade.requireCurrentUserId();
        return ResponseEntity.ok(annonceService.update(id, dto, currentUserId));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une annonce", description = "Supprime une annonce archivee (auteur uniquement)")
    @ApiResponse(responseCode = "204", description = "Annonce supprimee")
    @ApiResponse(responseCode = "403", description = "Non autorise (pas l'auteur ou annonce non archivee)",
            content = @Content(schema = @Schema(implementation = ApiErrorDTO.class),
                    examples = @ExampleObject(
                            value = "{\"error\": \"FORBIDDEN\", \"messages\": [\"Seule une annonce archivee peut etre supprimee\"]}")))
    @ApiResponse(responseCode = "404", description = "Annonce non trouvee",
            content = @Content(schema = @Schema(implementation = ApiErrorDTO.class)))
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID de l'annonce", example = "1") @PathVariable Long id) {
        UUID currentUserId = securityContextFacade.requireCurrentUserId();
        annonceService.delete(id, currentUserId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Changer le statut", description = "Change le statut d'une annonce (publish, archive)")
    @ApiResponse(responseCode = "200", description = "Statut modifie")
    @ApiResponse(responseCode = "403", description = "Non autorise",
            content = @Content(schema = @Schema(implementation = ApiErrorDTO.class)))
    @ApiResponse(responseCode = "409", description = "Transition de statut invalide",
            content = @Content(schema = @Schema(implementation = ApiErrorDTO.class),
                    examples = @ExampleObject(
                            value = "{\"error\": \"CONFLICT\", \"messages\": [\"Transition invalide: impossible d'appliquer 'publish' sur le statut PUBLISHED\"]}")))
    public ResponseEntity<AnnonceResponseDTO> changeStatus(
            @Parameter(description = "ID de l'annonce", example = "1") @PathVariable Long id,
            @Valid @RequestBody AnnonceStatusDTO dto) {
        UUID currentUserId = securityContextFacade.requireCurrentUserId();
        if ("archive".equals(dto.action())) {
            return ResponseEntity.ok(annonceService.archive(id, currentUserId));
        }
        return ResponseEntity.ok(annonceService.changeStatus(id, dto.action(), currentUserId));
    }
}

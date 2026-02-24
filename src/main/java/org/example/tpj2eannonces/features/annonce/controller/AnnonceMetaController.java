package org.example.tpj2eannonces.features.annonce.controller;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.example.tpj2eannonces.features.annonce.model.Annonce;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/meta/annonces")
@Tag(name = "Meta", description = "Metadonnees des annonces (champs triables, filtrables, recherchables)")
public class AnnonceMetaController {

    private static final List<String> SORTABLE_FIELDS = buildSortableFields();
    private static final List<String> FILTERABLE_FIELDS = List.of("q", "status", "categoryId", "authorId", "fromDate", "toDate");
    private static final List<String> SEARCHABLE_STRING_FIELDS = buildSearchableStringFields();

    @GetMapping
    @Operation(summary = "Metadonnees des annonces", description = "Retourne les champs triables, filtrables et recherchables")
    @ApiResponse(responseCode = "200", description = "Metadonnees retournees")
    public ResponseEntity<Map<String, Object>> getMetadata() {
        return ResponseEntity.ok(Map.of(
                "sortableFields", SORTABLE_FIELDS,
                "filterableFields", FILTERABLE_FIELDS,
                "searchableFields", SEARCHABLE_STRING_FIELDS
        ));
    }

    private static List<String> buildSortableFields() {
        return Arrays.stream(Annonce.class.getDeclaredFields())
                .map(Field::getName)
                .filter(name -> !name.equals("serialVersionUID"))
                .toList();
    }

    private static List<String> buildSearchableStringFields() {
        return Arrays.stream(Annonce.class.getDeclaredFields())
                .filter(f -> f.getType().equals(String.class))
                .map(Field::getName)
                .toList();
    }
}

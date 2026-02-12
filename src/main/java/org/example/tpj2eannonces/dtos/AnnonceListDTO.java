package org.example.tpj2eannonces.dtos;

import java.util.List;

import org.example.tpj2eannonces.model.Annonce;

public record AnnonceListDTO(List<Annonce> annonces, long totalCount) {
}

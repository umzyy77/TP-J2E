package org.example.tpj2eannonces.api.mapper;

import java.util.List;

import org.example.tpj2eannonces.api.dto.annonce.AnnonceCreateDTO;
import org.example.tpj2eannonces.api.dto.annonce.AnnonceResponseDTO;
import org.example.tpj2eannonces.api.dto.annonce.AnnonceUpdateDTO;
import org.example.tpj2eannonces.model.Annonce;
import org.example.tpj2eannonces.model.Category;
import org.example.tpj2eannonces.model.User;

public final class AnnonceMapper {

    private AnnonceMapper() {
    }

    public static Annonce toEntity(AnnonceCreateDTO dto) {
        Annonce annonce = new Annonce();
        annonce.setTitle(dto.title());
        annonce.setDescription(dto.description());
        annonce.setAdress(dto.adress());
        annonce.setMail(dto.mail());
        return annonce;
    }

    public static void updateEntity(Annonce annonce, AnnonceUpdateDTO dto) {
        annonce.setTitle(dto.title());
        annonce.setDescription(dto.description());
        annonce.setAdress(dto.adress());
        annonce.setMail(dto.mail());
    }

    public static AnnonceResponseDTO toResponseDTO(Annonce annonce) {
        AnnonceResponseDTO.Builder builder = AnnonceResponseDTO.builder()
                .id(annonce.getId())
                .title(annonce.getTitle())
                .description(annonce.getDescription())
                .adress(annonce.getAdress())
                .mail(annonce.getMail())
                .date(annonce.getDate())
                .status(annonce.getStatus() != null ? annonce.getStatus().name() : null);

        User author = annonce.getAuthor();
        if (author != null) {
            builder.author(new AnnonceResponseDTO.AuthorDTO(author.getId(), author.getUsername()));
        }

        Category category = annonce.getCategory();
        if (category != null) {
            builder.category(new AnnonceResponseDTO.CategoryDTO(category.getId(), category.getLabel()));
        }

        return builder.build();
    }

    public static List<AnnonceResponseDTO> toResponseDTOList(List<Annonce> annonces) {
        return annonces.stream()
                .map(AnnonceMapper::toResponseDTO)
                .toList();
    }
}

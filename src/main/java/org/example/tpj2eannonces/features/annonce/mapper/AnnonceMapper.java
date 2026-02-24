package org.example.tpj2eannonces.features.annonce.mapper;

import java.util.List;

import org.example.tpj2eannonces.features.annonce.dto.AnnonceFormDTO;
import org.example.tpj2eannonces.features.annonce.dto.AnnonceResponseDTO;
import org.example.tpj2eannonces.features.annonce.model.Annonce;
import org.example.tpj2eannonces.features.annonce.model.AnnonceStatus;
import org.example.tpj2eannonces.features.category.model.Category;
import org.example.tpj2eannonces.features.user.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface AnnonceMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "date", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "category", ignore = true)
    Annonce toEntity(AnnonceFormDTO dto);

    @Mapping(source = "status", target = "status", qualifiedByName = "statusToString")
    @Mapping(source = "author", target = "author")
    @Mapping(source = "category", target = "category")
    AnnonceResponseDTO toResponseDTO(Annonce annonce);

    List<AnnonceResponseDTO> toResponseDTOList(List<Annonce> annonces);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "date", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "category", ignore = true)
    void updateEntityFromDTO(AnnonceFormDTO dto, @MappingTarget Annonce annonce);

    @Named("statusToString")
    default String statusToString(AnnonceStatus status) {
        return status != null ? status.name() : null;
    }

    default AnnonceResponseDTO.AuthorDTO map(User user) {
        if (user == null) return null;
        return new AnnonceResponseDTO.AuthorDTO(user.getId(), user.getUsername());
    }

    default AnnonceResponseDTO.CategoryDTO map(Category category) {
        if (category == null) return null;
        return new AnnonceResponseDTO.CategoryDTO(category.getId(), category.getLabel());
    }
}

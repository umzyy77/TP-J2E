package org.example.tpj2eannonces.features.category.mapper;

import java.util.List;

import org.example.tpj2eannonces.features.category.dto.CategoryResponseDTO;
import org.example.tpj2eannonces.features.category.model.Category;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    CategoryResponseDTO toResponseDTO(Category category);

    List<CategoryResponseDTO> toResponseDTOList(List<Category> categories);
}

package org.example.tpj2eannonces.features.category.mapper;

import java.util.List;

import org.example.tpj2eannonces.features.category.dto.CategoryResponseDTO;
import org.example.tpj2eannonces.features.category.model.Category;
import org.mapstruct.factory.Mappers;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CategoryMapperTest {

    private final CategoryMapper mapper = Mappers.getMapper(CategoryMapper.class);

    @Test
    void toResponseDTO_shouldMapFields() {
        Category category = new Category("Immobilier");
        category.setId(1L);

        CategoryResponseDTO dto = mapper.toResponseDTO(category);

        assertThat(dto.id()).isEqualTo(1L);
        assertThat(dto.label()).isEqualTo("Immobilier");
    }

    @Test
    void toResponseDTOList_shouldMapList() {
        Category c1 = new Category("Immobilier");
        c1.setId(1L);
        Category c2 = new Category("Auto");
        c2.setId(2L);

        List<CategoryResponseDTO> dtos = mapper.toResponseDTOList(List.of(c1, c2));

        assertThat(dtos).hasSize(2);
        assertThat(dtos.get(0).label()).isEqualTo("Immobilier");
        assertThat(dtos.get(1).label()).isEqualTo("Auto");
    }

    @Test
    void toResponseDTO_shouldHandleNull() {
        assertThat(mapper.toResponseDTO(null)).isNull();
    }
}

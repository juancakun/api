package com.gestion.eventos.api.mapper;

import com.gestion.eventos.api.DTO.CategoryDto;
import com.gestion.eventos.api.domain.Category;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    CategoryDto toDto(Category category);
    Category toEntity(CategoryDto categoryDto);

}

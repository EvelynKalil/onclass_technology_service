package com.onclass.technology.infrastructure.entrypoints.mapper;

import com.onclass.technology.domain.model.Technology;
import com.onclass.technology.infrastructure.entrypoints.dto.TechnologyDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TechnologyMapper {

    // DTO → Dominio
    @Mapping(target = "id", ignore = true)
    @Mapping(source = "name",        target = "name")
    @Mapping(source = "description", target = "description")
    Technology dtoToDomain(TechnologyDTO dto);

    // Dominio → DTO
    @Mapping(source = "id",          target = "id")
    @Mapping(source = "name",        target = "name")
    @Mapping(source = "description", target = "description")
    TechnologyDTO toDto(Technology model);
}

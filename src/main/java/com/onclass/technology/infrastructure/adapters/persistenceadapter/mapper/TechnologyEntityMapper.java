package com.onclass.technology.infrastructure.adapters.persistenceadapter.mapper;

import com.onclass.technology.domain.model.Technology;
import com.onclass.technology.infrastructure.adapters.persistenceadapter.entity.TechnologyEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TechnologyEntityMapper {
    @Mapping(source = "id",          target = "id")
    @Mapping(source = "name",        target = "name")
    @Mapping(source = "description", target = "description")
    Technology toModel(TechnologyEntity entity);
    TechnologyEntity toEntity(Technology model);
}

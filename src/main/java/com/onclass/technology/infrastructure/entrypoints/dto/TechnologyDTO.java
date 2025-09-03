package com.onclass.technology.infrastructure.entrypoints.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class TechnologyDTO {
    private Long id;
    private String name;
    private String description;
}

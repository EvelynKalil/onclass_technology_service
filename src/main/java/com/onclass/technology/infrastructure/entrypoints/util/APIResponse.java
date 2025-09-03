package com.onclass.technology.infrastructure.entrypoints.util;

import com.onclass.technology.infrastructure.entrypoints.dto.TechnologyDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
public class APIResponse {
    private String code;
    private String message;
    private String identifier;
    private String date;
    private TechnologyDTO data;
    private List<ErrorDTO> errors;
}

package com.onclass.technology.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum TechnicalMessage {

    // Generic
    INTERNAL_ERROR("500", "Something went wrong, please try again", ""),
    INTERNAL_ERROR_IN_ADAPTERS("PRC501", "Something went wrong in adapters, please try again", ""),
    INVALID_REQUEST("400", "Bad Request, please verify data", ""),
    INVALID_PARAMETERS(INVALID_REQUEST.code, "Bad Parameters, please verify data", ""),
    UNSUPPORTED_OPERATION("501", "Method not supported, please try again", ""),

    // Technology (HU1)
    TECHNOLOGY_CREATED("201", "Technology created successfully", ""),
    TECHNOLOGY_ALREADY_EXISTS("400", "Technology name already exists", "name"),
    TECHNOLOGY_NAME_REQUIRED("400", "Technology name is required", "name"),
    TECHNOLOGY_DESCRIPTION_REQUIRED("400", "Technology description is required", "description"),
    TECHNOLOGY_NAME_TOO_LONG("400", "Technology name max length is 50", "name"),
    TECHNOLOGY_DESCRIPTION_TOO_LONG("400", "Technology description max length is 90", "description"),
    ADAPTER_RESPONSE_NOT_FOUND("404", "Technology not found", "id");

    public final String code;
    public final String message;
    public final String param;
}

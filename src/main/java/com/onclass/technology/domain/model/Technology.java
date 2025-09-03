package com.onclass.technology.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Technology {
    private Long id;
    private String name;
    private String description;
}
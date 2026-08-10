package com.edugauge.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class CategoryUpdateRequest {
    @NotBlank
    private String name;
}
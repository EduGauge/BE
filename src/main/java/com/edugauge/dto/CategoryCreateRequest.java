package com.edugauge.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class CategoryCreateRequest {
    @NotBlank
    private String name;

}

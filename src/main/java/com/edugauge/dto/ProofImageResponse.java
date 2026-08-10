package com.edugauge.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ProofImageResponse {

    private Long imageId;
    private String imageUrl;
    private LocalDateTime createdAt;
}

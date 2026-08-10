package com.edugauge.dto;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class ProofImageCreateRequest {
    private LocalDate proofDate;
    private String imageUrl;
}

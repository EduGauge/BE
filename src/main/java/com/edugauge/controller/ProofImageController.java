package com.edugauge.controller;


import com.edugauge.dto.ProofImageResponse;
import com.edugauge.service.ProofImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/proof-images")
public class ProofImageController {
    private final ProofImageService proofImageService;
    @PostMapping(consumes = "multipart/form-data")
    public ProofImageResponse createProofImage(
            @AuthenticationPrincipal Long userId,
            @RequestPart("image") MultipartFile image
    ) {
        return proofImageService.createProofImage(userId, image);
    }
}
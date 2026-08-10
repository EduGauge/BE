package com.edugauge.service;

import com.edugauge.domain.ProofImage;
import com.edugauge.domain.user.User;
import com.edugauge.dto.ProofImageResponse;
import com.edugauge.repositiry.ProofImageRepository;
import com.edugauge.repositiry.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ProofImageService {
    private final ProofImageRepository proofImageRepository;
    private final UserRepository userRepository;

    public ProofImageResponse createProofImage(
            Long userId,
            MultipartFile image
    ) {
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException("사용자를 찾을 수 없습니다")
                );

        LocalDate proofDate = getProofDate();

        Optional<ProofImage> existing =
                proofImageRepository.findByUser_IdAndProofDate(
                        userId,
                        proofDate
                );

        if (existing.isPresent()) {
            throw new IllegalArgumentException("이미 해당 날짜에 인증샷이 존재합니다");
        }

        String imageUrl = saveImage(image);

        ProofImage proofImage = new ProofImage(
                user,
                proofDate,
                imageUrl
        );

        ProofImage saved = proofImageRepository.save(proofImage);

        return new ProofImageResponse(
                saved.getId(),
                saved.getImageUrl(),
                saved.getCreatedAt()
        );
    }

    private LocalDate getProofDate() {
        LocalDate today = LocalDate.now();

        if (LocalTime.now().isBefore(LocalTime.of(6, 0))) {
            return today.minusDays(1);
        }

        return today;
    }

    private String saveImage(MultipartFile image) {
        if (image.isEmpty()) {
            throw new IllegalArgumentException("이미지 파일이 비어 있습니다");
        }

        String contentType = image.getContentType();

        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("이미지 파일만 업로드할 수 있습니다");
        }

        try {
            String originalFilename = image.getOriginalFilename();
            String extension = getExtension(originalFilename);
            String storedFilename = UUID.randomUUID() + extension;

            Path uploadDir = Paths.get("uploads", "proof-images");
            Files.createDirectories(uploadDir);

            Path filePath = uploadDir.resolve(storedFilename);
            image.transferTo(filePath.toFile());

            return "/uploads/proof-images/" + storedFilename;

        } catch (IOException e) {
            throw new IllegalArgumentException("이미지 저장에 실패했습니다");
        }
    }

    private String getExtension(String originalFilename) {
        if (originalFilename == null || !originalFilename.contains(".")) {
            return "";
        }

        return originalFilename.substring(originalFilename.lastIndexOf("."));
    }
}
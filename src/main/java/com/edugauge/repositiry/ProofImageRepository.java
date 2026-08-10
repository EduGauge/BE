package com.edugauge.repositiry;

import com.edugauge.domain.ProofImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface ProofImageRepository extends JpaRepository<ProofImage, Long> {
    Optional<ProofImage> findByUser_IdAndProofDate(
            Long userId,
            LocalDate proofDate
    );
    void deleteByUser_Id(Long userId);
}

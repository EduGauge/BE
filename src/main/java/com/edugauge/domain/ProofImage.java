package com.edugauge.domain;

import com.edugauge.domain.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "proof_images",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_proof_image_user_date",
                        columnNames = {"user_id", "proof_date"}
                )
        }
)
public class ProofImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "proof_image_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "proof_date", nullable = false)
    private LocalDate proofDate;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    public ProofImage(
            User user,
            LocalDate proofDate,
            String imageUrl
    ) {
        this.user = user;
        this.proofDate = proofDate;
        this.imageUrl = imageUrl;
    }

}
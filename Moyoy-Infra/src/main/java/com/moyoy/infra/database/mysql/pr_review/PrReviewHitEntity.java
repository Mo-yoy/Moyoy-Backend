package com.moyoy.infra.database.mysql.pr_review;

import com.moyoy.infra.database.mysql.support.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "pr_review_hits",
    uniqueConstraints = @UniqueConstraint(
        name = "uq_user_review_hit",
        columnNames = {"user_id", "pr_review_id"}
    )
)
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PrReviewHitEntity extends BaseTimeEntity {

    @Id
    @Column(name = "pr_review_hit_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long prReviewId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private LocalDateTime lastIncreasedAt;
}

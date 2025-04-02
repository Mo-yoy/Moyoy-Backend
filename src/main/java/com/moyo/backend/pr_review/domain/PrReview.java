package com.moyo.backend.pr_review.domain;

import com.moyo.backend.common.entity.BaseTimeEntity;
import com.moyo.backend.pr_review.domain.position.Position;
import com.moyo.backend.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "pr_review")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PrReview extends BaseTimeEntity {

    @Id
    @Column(name = "pr_review_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Lob
    @Column(nullable = false)
    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    private String content;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private String prUrl;

    // 직군 태그는 null이 가능하므로 enum 대신 String 선택.
    @Column(nullable = true)
    private Position position;

    @Column(nullable = false)
    private int hitCount;

    @Column(nullable = false)
    private Boolean status; // open, closed 여부.

    @Builder
    public PrReview(String title, String content, User user, String prUrl, Position position, int hitCount, boolean status) {
        this.title = title;
        this.content = content;
        this.user = user;
        this.prUrl = prUrl;
        this.position = position;
        this.hitCount = hitCount;
        this.status = status;
    }

    public void increaseHitCount() {
        this.hitCount++;
    }

    public void updateDetail(String title, String content, String prUrl, Position position) {
        this.title = title;
        this.content = content;
        this.prUrl = prUrl;
        this.position = position;
    }
}

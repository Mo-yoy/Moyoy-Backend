package com.moyo.backend.pr_review.domain;

import com.moyo.backend.common.entity.BaseTimeEntity;
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
    private String position;

    @Column(nullable = false)
    private int hitCount;

    @Column(nullable = false)
//    private boolean isCompleted; // 접두사 is 들어가면 직렬화와 jpa 사용에 헷갈림, Boolean도 있지만 status로 통합.
    private Boolean status; // 마감 여부.

    @Builder
    public PrReview(String title, String content, User user, String prUrl, String position, int hitCount, boolean status) {
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

    public void updateDetail(String title, String content, String prUrl, String position) {
        this.title = title;
        this.content = content;
        this.prUrl = prUrl;
        this.position = position;
    }
}

package com.moyoy.infra.database.pr_review;

import com.moyoy.domain.pr_review.Position;
import com.moyoy.domain.pr_review.Status;
import com.querydsl.core.annotations.QueryProjection;

import java.time.LocalDateTime;

public class PrReviewWithAuthorView {

    private Long id;
    private Long userId;
    private Integer githubUserId;
    private String username;
    private String profileImgUrl;
    private String title;
    private String content;
    private String prUrl;
    private Position position;
    private int hitCount;
    private Status status;
    private boolean adopted;
    private LocalDateTime deadline;

    @QueryProjection
    public PrReviewWithAuthorView(Long id,
                                  Long userId,
                                  Integer githubUserId,
                                  String username,
                                  String profileImgUrl,
                                  String title,
                                  String content,
                                  String prUrl,
                                  Position position,
                                  int hitCount,
                                  Status status,
                                  boolean adopted,
                                  LocalDateTime deadline) {
        this.id = id;
        this.userId = userId;
        this.githubUserId = githubUserId;
        this.username = username;
        this.profileImgUrl = profileImgUrl;
        this.title = title;
        this.content = content;
        this.prUrl = prUrl;
        this.position = position;
        this.hitCount = hitCount;
        this.status = status;
        this.adopted = adopted;
        this.deadline = deadline;
    }
}

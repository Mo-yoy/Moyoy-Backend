package com.moyo.backend.pr_review.dto;

import lombok.Getter;

import java.time.Duration;
import java.time.LocalDateTime;

@Getter
public class PrReviewDto {

    private final String profileImageUrl;
    private final String username;
    private final String position;
    private final String title;
    private final int hitCount;
    private final String since; // 이건 자체적으로 계산.

    public PrReviewDto(String profileImageUrl, String username, String position, String title, Integer hitCount, LocalDateTime createdAt) {
        this.profileImageUrl = profileImageUrl;
        this.username = username;
        this.position = position;
        this.title = title;
        this.hitCount = hitCount != null ? hitCount : 0;
        this.since = calculateTimeAgo(createdAt);
    }

    // createdAt과 지금의 간격을 계산.
    private String calculateTimeAgo(LocalDateTime createdAt) {
        LocalDateTime now = LocalDateTime.now();

        // ChronoUnit 인식이 안돼서 Duration으로 대체.
        Duration duration = Duration.between(createdAt, now);

        long minutes = duration.toMinutes();
        long hours = duration.toHours();
        long days = duration.toDays();
        long months = days / 30;
        long years = days / 365;

        if (minutes < 60) {
            return minutes + "분 전";
        } else if (hours < 24) {
            return hours + "시간 전";
        } else if (days < 30) {
            return days + "일 전";
        } else if (months < 12) {
            return months + "달 전";
        } else {
            return years + "년 전";
        }
    }
}

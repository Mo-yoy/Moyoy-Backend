package com.moyo.backend.domain.github_follow.business;

import com.moyo.backend.domain.github_follow.implement.DetectType;

public record GithubFollowDetection(
    DetectType detectType,
    Long lastUserId,
    int size,
    boolean forceSync) {
    
    public GithubFollowDetection(
            String detectType,
            Long lastUserId,
            int size,
            boolean forceSync) {

        this(
            parseDetectType(detectType),
            lastUserId,
            size,
            forceSync
        );
    }

    private static DetectType parseDetectType(String detectType) {
        try {
            return DetectType.valueOf(detectType.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("유효하지 않은 detectType 입니다: " + detectType);
        }
    }
}
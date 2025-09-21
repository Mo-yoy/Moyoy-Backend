package com.moyoy.domain.pr_review;

/// TODO : 트레이드 오프는 일단 없이 가는걸로
public record Author(
        Long id,
        String username,
        String profileImgUrl
) {
    public static Author createInitial(Long id) {
        return new Author(id, null, null);
    }
}

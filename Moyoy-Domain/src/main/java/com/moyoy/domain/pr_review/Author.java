package com.moyoy.domain.pr_review;

public record Author(
        Long id,
        Integer githubUserId,
        String username,
        String profileImgUrl
) {
    public static Author createInitial(Long id) {
        return new Author(id, null, null, null);
    }
}

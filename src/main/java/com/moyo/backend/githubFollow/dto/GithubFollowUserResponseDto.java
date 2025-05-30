package com.moyo.backend.githubFollow.dto;

import com.moyo.backend.githubFollow.domain.GithubFollowUser;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GithubFollowUserResponseDto {
    private Long id;
    private String username;
    private String avatarUrl;

    public static GithubFollowUserResponseDto from(GithubFollowUser user) {
        return GithubFollowUserResponseDto.builder()
                .id(user.id())
                .username(user.username())
                .avatarUrl(user.profileImgUrl())
                .build();
    }
}
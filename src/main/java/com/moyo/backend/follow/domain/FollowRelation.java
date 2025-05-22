package com.moyo.backend.follow.domain;

import com.moyo.backend.follow.dto.response.GithubFollowUserInfoResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.*;


@Getter
@NoArgsConstructor
public class FollowRelation {

    private Long userId;
    private List<GithubFollowUserInfoResponse> githubFollowers;
    private List<GithubFollowUserInfoResponse> githubFollowings;
    private LocalDateTime createdAt;

    @Builder
    private FollowRelation(Long userId, List<GithubFollowUserInfoResponse> githubFollowers, List<GithubFollowUserInfoResponse> githubFollowings, LocalDateTime createdAt) {
        this.userId = userId;
        this.githubFollowers = githubFollowers;
        this.githubFollowings = githubFollowings;
        this.createdAt = createdAt;
    }

    public List<GithubFollowUserInfoResponse> filterUsersByDetectType(DetectType detectType){

        // 깃허브 API의 기본 정렬 순서는 userId ASC, 이를 그대로 사용
        Set<GithubFollowUserInfoResponse> followerSet = new LinkedHashSet<>(githubFollowers);
        Set<GithubFollowUserInfoResponse> followingSet = new LinkedHashSet<>(githubFollowings);
        Set<GithubFollowUserInfoResponse> resultSet = new LinkedHashSet<>();

        switch (detectType) {
            case MUTUAL -> {
                followingSet.retainAll(followerSet);
                resultSet = followingSet;
            }
            case FOLLOW_ONLY -> {
                followingSet.removeAll(followerSet);
                resultSet = followingSet;
            }
            case FOLLOWED_ONLY -> {
                followerSet.removeAll(followingSet);
                resultSet = followerSet;
            }
        }

        return new ArrayList<>(resultSet);
    }
}

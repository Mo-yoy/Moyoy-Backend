package com.moyo.backend.follow.domain;

import com.moyo.backend.follow.dto.response.GithubFollowUserInfoResponse;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.*;


@Getter
@Builder
public class FollowRelation {

    private Long userId;
    private List<GithubFollowUserInfoResponse> githubFollowers;
    private List<GithubFollowUserInfoResponse> githubFollowings;
    private LocalDateTime createdAt;


    public List<GithubFollowUserInfoResponse> filterUsersByDetectType(String detectType){

        // 깃허브 API의 기본 정렬 순서는 userId ASC, 이를 그대로 사용
        Set<GithubFollowUserInfoResponse> followerSet = new LinkedHashSet<>(githubFollowers);
        Set<GithubFollowUserInfoResponse> followingSet = new LinkedHashSet<>(githubFollowings);
        Set<GithubFollowUserInfoResponse> resultSet = new LinkedHashSet<>();

        switch (detectType) {
            case "mutual" -> {
                followingSet.retainAll(followerSet);
                resultSet = followingSet;
            }
            case "following-only" -> {
                followingSet.removeAll(followerSet);
                resultSet = followingSet;
            }
            case "follower-only" -> {
                followerSet.removeAll(followingSet);
                resultSet = followerSet;
            }
        }

        return new ArrayList<>(resultSet);
    }
}

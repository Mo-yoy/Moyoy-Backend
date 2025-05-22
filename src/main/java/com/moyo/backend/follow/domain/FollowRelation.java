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


    // DetectType 에 맞게 필터링
    public List<GithubFollowUserInfoResponse> filterUsersByDetectType(DetectType detectType) {

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

    public void addFollowing(GithubFollowUserInfoResponse user) {

        boolean alreadyExist = githubFollowings.stream().anyMatch(info -> info.getId().equals(user.getId()));
        if(alreadyExist) return;

        Long targetId = user.getId();

        // 나 보다 큰 Id를 찾았다면 그곳에 삽입
        for (int insertIndex = 0; insertIndex < githubFollowings.size(); insertIndex++) {
            if (githubFollowings.get(insertIndex).getId() > targetId) {
                githubFollowings.add(insertIndex, user);
                return;
            }
        }
        
        // 끝까지 못 찾았다면 가장 뒤에 삽입
        githubFollowings.add(user);
    }

    public void addFollower(GithubFollowUserInfoResponse user){

        boolean alreadyExist = githubFollowers.stream().anyMatch(info -> info.getId().equals(user.getId()));
        if(alreadyExist) return;

        Long targetId = user.getId();

        for (int insertIndex = 0; insertIndex < githubFollowers.size(); insertIndex++) {
            if (githubFollowers.get(insertIndex).getId() > targetId) {
                githubFollowers.add(insertIndex, user);
                return;
            }
        }

        githubFollowers.add(user);
    }

    public void removeFollowing(Long userId) {

        for (int idx = 0; idx < githubFollowings.size(); idx++) {

            if (githubFollowings.get(idx).getId().equals(userId)) {
                githubFollowings.remove(idx);
                break;
            }
        }
    }

    public void removeFollower(Long userId){


        for (int idx = 0; idx < githubFollowers.size(); idx++) {

            if (githubFollowers.get(idx).getId().equals(userId)) {
                githubFollowers.remove(idx);
                break;
            }
        }
    }
}
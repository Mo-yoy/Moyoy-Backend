package com.moyo.backend.githubFollow.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;


@Getter
@NoArgsConstructor
public class GithubFollowRelation {

    private Long userId;
    
    // 팔로워, 팔로잉 리스트는 깃허브 응답을 그대로 저장 하기 때문에 Id 오름차 순
    private List<GithubFollowUser> githubFollowers;
    private List<GithubFollowUser> githubFollowings;
    private LocalDateTime createdAt;

    @Builder
    private GithubFollowRelation(Long userId, List<GithubFollowUser> githubFollowers, List<GithubFollowUser> githubFollowings, LocalDateTime createdAt) {
        this.userId = userId;
        this.githubFollowers = githubFollowers;
        this.githubFollowings = githubFollowings;
        this.createdAt = createdAt;
    }

    public List<GithubFollowUser> filterUsersByDetectType(DetectType detectType) {

        // 깃허브 API의 기본 정렬 순서는 userId ASC, 이를 그대로 사용
        Set<GithubFollowUser> followerSet = new LinkedHashSet<>(githubFollowers);
        Set<GithubFollowUser> followingSet = new LinkedHashSet<>(githubFollowings);
        Set<GithubFollowUser> resultSet = new LinkedHashSet<>();

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

    public void addFollowing(GithubFollowUser user) {

        boolean alreadyExist = githubFollowings.stream().anyMatch(snapshot -> snapshot.id().equals(user.id()));
        if(alreadyExist) return;

        Long targetId = user.id();

        // 나 보다 큰 Id를 찾았다면 그곳에 삽입
        for (int insertIndex = 0; insertIndex < githubFollowings.size(); insertIndex++) {
            if (githubFollowings.get(insertIndex).id() > targetId) {
                githubFollowings.add(insertIndex, user);
                return;
            }
        }
        
        // 끝까지 못 찾았다면 가장 뒤에 삽입
        githubFollowings.add(user);
    }

    public void addFollower(GithubFollowUser user){

        boolean alreadyExist = githubFollowers.stream().anyMatch(snapshot -> snapshot.id().equals(user.id()));
        if(alreadyExist) return;

        Long targetId = user.id();

        for (int insertIndex = 0; insertIndex < githubFollowers.size(); insertIndex++) {
            if (githubFollowers.get(insertIndex).id() > targetId) {
                githubFollowers.add(insertIndex, user);
                return;
            }
        }

        githubFollowers.add(user);
    }

    public void removeFollowing(Long userId) {

        for (int idx = 0; idx < githubFollowings.size(); idx++) {

            if (githubFollowings.get(idx).id().equals(userId)) {
                githubFollowings.remove(idx);
                break;
            }
        }
    }

    public void removeFollower(Long userId){

        for (int idx = 0; idx < githubFollowers.size(); idx++) {

            if (githubFollowers.get(idx).id().equals(userId)) {
                githubFollowers.remove(idx);
                break;
            }
        }
    }
}
package com.moyo.backend.githubFollow.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

import java.lang.reflect.Array;
import java.time.LocalDateTime;
import java.util.*;


@Getter
@NoArgsConstructor
public class GithubFollowRelation {

    private Long userId;
    
    // 팔로워, 팔로잉 리스트는 깃허브 응답을 그대로 저장 하고 Id 오름차 순으로 저장됨
    private TreeSet<GithubFollowUser> githubFollowers;
    private TreeSet<GithubFollowUser> githubFollowings;
    private LocalDateTime createdAt;

    @Builder(access = AccessLevel.PRIVATE)
    private GithubFollowRelation(Long userId, TreeSet<GithubFollowUser> githubFollowers, TreeSet<GithubFollowUser> githubFollowings, LocalDateTime createdAt) {
        this.userId = userId;
        this.githubFollowers = githubFollowers;
        this.githubFollowings = githubFollowings;
        this.createdAt = createdAt;
    }

    public static GithubFollowRelation create(Long userId, List<GithubFollowUser> githubFollowers,List<GithubFollowUser> githubFollowings){
        return GithubFollowRelation.builder()
                .userId(userId)
                .githubFollowers(new TreeSet<>(githubFollowers))
                .githubFollowings(new TreeSet<>(githubFollowings))
                .createdAt(LocalDateTime.now())
                .build();
    }

    public List<GithubFollowUser> filterUsersByDetectType(DetectType detectType) {

        Set<GithubFollowUser> resultSet = new TreeSet<>();

        switch (detectType) {
            case MUTUAL -> {
                githubFollowings.retainAll(githubFollowers);
                resultSet = githubFollowings;
            }
            case FOLLOW_ONLY -> {
                githubFollowings.removeAll(githubFollowers);
                resultSet = githubFollowings;
            }
            case FOLLOWED_ONLY -> {
                githubFollowers.removeAll(githubFollowings);
                resultSet = githubFollowers;
            }
        }

        return new ArrayList<>(resultSet);
    }

    public void addFollowing(GithubFollowUser user) {

        githubFollowings.add(user);
    }

    public void addFollower(GithubFollowUser user){

        githubFollowers.add(user);
    }

    public void removeFollowing(GithubFollowUser user) {

        githubFollowings.remove(user);
    }

    public void removeFollower(GithubFollowUser user){

        githubFollowers.remove(user);
    }
}
package com.moyo.backend.follow.infrastructure.repository;

import com.moyo.backend.follow.application.FollowRepository;
import com.moyo.backend.follow.domain.entity.GithubUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FollowRepositoryImpl implements FollowRepository {

    private final GithubUserJpaRepository githubUserJpaRepository;

    @Override
    public Slice<GithubUser> findMutualFollowGithubUsers(Long userId, Pageable pageable) {
        return githubUserJpaRepository.findMutualFollowingGithubUsers(userId, pageable);
    }

    @Override
    public Slice<GithubUser> findFollowerOnlyList(Long userId, Pageable pageable) {
        return githubUserJpaRepository.findFollowerOnlyList(userId,pageable);
    }

    @Override
    public Slice<GithubUser> findFollowingOnlyList(Long userId, Pageable pageable) {
        return githubUserJpaRepository.findFollowingOnlyList(userId, pageable);
    }
}

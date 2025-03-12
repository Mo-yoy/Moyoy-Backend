package com.moyo.backend.follow.application;

import com.moyo.backend.follow.domain.entity.GithubUser;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface FollowRepository {
    Slice<GithubUser> findMutualFollowGithubUsers(Long userId, Pageable pageable);
    Slice<GithubUser> findFollowerOnlyList(Long userId, Pageable pageable);
    Slice<GithubUser> findFollowingOnlyList(Long userId, Pageable pageable);
}

package com.moyo.backend.follow.application;

import com.moyo.backend.follow.domain.entity.GithubUser;
import com.moyo.backend.follow.ui.dto.FollowListResponse;
import com.moyo.backend.follow.ui.dto.GithubUserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class FollowService {

    private final FollowRepository followRepository;

    public FollowListResponse getMutualFollowingList(Long userId, Pageable pageable) {

        Slice<GithubUser> mutualFollowGithubUsers = followRepository.findMutualFollowGithubUsers(userId, pageable);

        List<GithubUserDto> userDtoList = mutualFollowGithubUsers.getContent().stream()
                .map(githubUser -> new GithubUserDto(
                        githubUser.getGitHubUserInfo().getUsername(),
                        githubUser.getGitHubUserInfo().getProfileImgUrl())
                )
                .toList();

        return new FollowListResponse(userDtoList,mutualFollowGithubUsers.isLast());
    }

    public FollowListResponse getFollowerOnlyList(Long userId, Pageable pageable) {

        Slice<GithubUser> followerOnlyList = followRepository.findFollowerOnlyList(userId, pageable);

        Slice<GithubUserDto> userDtoSlice = followerOnlyList.map(githubUser ->
                new GithubUserDto(githubUser.getGitHubUserInfo().getUsername(), githubUser.getGitHubUserInfo().getProfileImgUrl())
        );

        return new FollowListResponse(userDtoSlice.getContent(),followerOnlyList.isLast());
    }

    public FollowListResponse getFollowingOnlyList(Long userId, Pageable pageable) {

        Slice<GithubUser> followingOnlySlice = followRepository.findFollowingOnlyList(userId, pageable);

        Slice<GithubUserDto> userDtoSlice = followingOnlySlice.map(githubUser ->
                new GithubUserDto(githubUser.getGitHubUserInfo().getUsername(), githubUser.getGitHubUserInfo().getProfileImgUrl())
        );

        return new FollowListResponse(userDtoSlice.getContent(),followingOnlySlice.isLast());
    }
}
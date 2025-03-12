package com.moyo.backend.follow.application;

import com.moyo.backend.follow.dto.GithubUserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

import static com.moyo.backend.common.constant.MoyoConstants.GITHUB_REGISTRATION_ID;

@Service
@Transactional
@RequiredArgsConstructor
public class FollowQueryService {

    private final GithubFollowQueryClient githubFollowQueryClient;
    private final OAuth2AuthorizedClientService oAuth2AuthorizedClientService;


    // 나만 팔로잉 = 나의 팔로잉 목록 - 나의 팔로워 목록
    public Set<GithubUserResponse> getFollowingOnlyList(GithubOAuth2User userPrincipal) {

        Set<GithubUserResponse> followingSet = new HashSet<>(githubFollowQueryClient.getFollowingList(getGithubOAuthAccessToken(userPrincipal)));
        Set<GithubUserResponse> followerSet = new HashSet<>(githubFollowQueryClient.getFollowerList(getGithubOAuthAccessToken(userPrincipal)));
        followingSet.removeAll(followerSet);
        return followingSet;
    }

    // 상대만 나를 팔로잉 = 나의 팔로워 목록 - 나의 팔로잉 목록
    public Set<GithubUserResponse> getFollowerOnlyList(GithubOAuth2User userPrincipal) {

        Set<GithubUserResponse> followerSet = new HashSet<>(githubFollowQueryClient.getFollowerList(getGithubOAuthAccessToken(userPrincipal)));
        Set<GithubUserResponse> followingSet = new HashSet<>(githubFollowQueryClient.getFollowingList(getGithubOAuthAccessToken(userPrincipal)));
        followerSet.removeAll(followingSet);
        return followerSet;
    }

    // 서로 팔로잉 = 나의 팔로워 목록 ⋂ 나의 팔로잉 목록
    public Set<GithubUserResponse> getMutualFollowList(GithubOAuth2User userPrincipal){

        Set<GithubUserResponse> followingSet = new HashSet<>(githubFollowQueryClient.getFollowingList(getGithubOAuthAccessToken(userPrincipal)));
        Set<GithubUserResponse> followerSet = new HashSet<>(githubFollowQueryClient.getFollowerList(getGithubOAuthAccessToken(userPrincipal)));
        followingSet.retainAll(followerSet);
        return followingSet;
    }

    private String getGithubOAuthAccessToken(GithubOAuth2User userPrincipal){

        return oAuth2AuthorizedClientService
                .loadAuthorizedClient(GITHUB_REGISTRATION_ID, userPrincipal.getName())
                .getAccessToken()
                .getTokenValue();
    }
}

package com.moyo.backend.follow.application;

import com.moyo.backend.follow.dto.GithubUserResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

import static com.moyo.backend.common.constant.MoyoConstants.GITHUB_REGISTRATION_ID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class FollowService {

    private final FollowRepository followRepository;
    private final OAuth2AuthorizedClientService oAuth2AuthorizedClientService;

    // 나만 팔로잉 = 나의 팔로잉 목록 - 나의 팔로워 목록
    public Set<GithubUserResponse> getFollowingOnlyList(GithubOAuth2User userPrincipal) {

        Set<GithubUserResponse> followingSet = new HashSet<>(followRepository.getFollowingList(getGithubOAuthAccessToken(userPrincipal)));
        Set<GithubUserResponse> followerSet = new HashSet<>(followRepository.getFollowerList(getGithubOAuthAccessToken(userPrincipal)));
        followingSet.removeAll(followerSet);
        return followingSet;
    }

    // 상대만 나를 팔로잉 = 나의 팔로워 목록 - 나의 팔로잉 목록
    public Set<GithubUserResponse> getFollowerOnlyList(GithubOAuth2User userPrincipal) {

        Set<GithubUserResponse> followerSet = new HashSet<>(followRepository.getFollowerList(getGithubOAuthAccessToken(userPrincipal)));
        Set<GithubUserResponse> followingSet = new HashSet<>(followRepository.getFollowingList(getGithubOAuthAccessToken(userPrincipal)));
        followerSet.removeAll(followingSet);
        return followerSet;
    }

    // 서로 팔로잉 = 나의 팔로워 목록 ⋂ 나의 팔로잉 목록
    public Set<GithubUserResponse> getMutualFollowList(GithubOAuth2User userPrincipal){

        Set<GithubUserResponse> followingSet = new HashSet<>(followRepository.getFollowingList(getGithubOAuthAccessToken(userPrincipal)));
        Set<GithubUserResponse> followerSet = new HashSet<>(followRepository.getFollowerList(getGithubOAuthAccessToken(userPrincipal)));
        followingSet.retainAll(followerSet);
        return followingSet;
    }

    public void follow(String username, GithubOAuth2User userPrincipal) {

        if(followRepository.follow(username, getGithubOAuthAccessToken(userPrincipal)) == 204)
            log.info("[Github 에서 실제 팔로우 성공] | 요청자 ProviderId: {} -> 대상: {}", userPrincipal.getName(),username);
    }

    public void unfollow(String username, GithubOAuth2User userPrincipal) {

        if(followRepository.unfollow(username, getGithubOAuthAccessToken(userPrincipal)) == 204)
            log.info("[Github 에서 실제 언 팔로우 성공] | 요청자 ProviderId: {} -> 대상: {}", userPrincipal.getName(),username);
    }

    private String getGithubOAuthAccessToken(GithubOAuth2User userPrincipal){

        return oAuth2AuthorizedClientService
                .loadAuthorizedClient(GITHUB_REGISTRATION_ID, userPrincipal.getName())
                .getAccessToken()
                .getTokenValue();
    }

}

package com.moyo.backend.follow.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.moyo.backend.common.constant.MoyoConstants.GITHUB_REGISTRATION_ID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class FollowCommandService {

    private final GithubFollowCommandClient githubFollowCommandClient;
    private final OAuth2AuthorizedClientService oAuth2AuthorizedClientService;

    public void follow(String username, GithubOAuth2User userPrincipal) {

        if(githubFollowCommandClient.follow(username, getGithubOAuthAccessToken(userPrincipal)) == 204)
            log.info("[Github 에서 실제 팔로우 성공] | 요청자 ProviderId: {} -> 대상: {}", userPrincipal.getName(),username);
    }

    public void unfollow(String username, GithubOAuth2User userPrincipal) {

        if(githubFollowCommandClient.unfollow(username, getGithubOAuthAccessToken(userPrincipal)) == 204)
            log.info("[Github 에서 실제 언 팔로우 성공] | 요청자 ProviderId: {} -> 대상: {}", userPrincipal.getName(),username);
    }

    private String getGithubOAuthAccessToken(GithubOAuth2User userPrincipal){

        return oAuth2AuthorizedClientService
                .loadAuthorizedClient(GITHUB_REGISTRATION_ID, userPrincipal.getName())
                .getAccessToken()
                .getTokenValue();
    }
}

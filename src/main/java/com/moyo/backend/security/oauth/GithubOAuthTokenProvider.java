package com.moyo.backend.security.oauth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.stereotype.Component;

import static com.moyo.backend.common.constant.MoyoConstants.GITHUB_REGISTRATION_ID;

@Component
@RequiredArgsConstructor
public class GithubOAuthTokenProvider {

    private final OAuth2AuthorizedClientService oAuth2AuthorizedClientService;

    public String getGithubAccessToken(Long userId){
        return oAuth2AuthorizedClientService.loadAuthorizedClient(GITHUB_REGISTRATION_ID, userId.toString()).getAccessToken().getTokenValue();
    }
}

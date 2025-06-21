package com.moyo.backend.domain.security.oauth;

import static com.moyo.backend.common.constant.MoyoConstants.GITHUB_REGISTRATION_ID;

import lombok.RequiredArgsConstructor;

import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GithubOAuthTokenReader {

	private final OAuth2AuthorizedClientService oAuth2AuthorizedClientService;

	public String getGithubAccessToken(Long userId) {
		return oAuth2AuthorizedClientService.loadAuthorizedClient(GITHUB_REGISTRATION_ID, userId.toString()).getAccessToken().getTokenValue();
	}
}

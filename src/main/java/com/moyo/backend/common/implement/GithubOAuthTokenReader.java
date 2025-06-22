package com.moyo.backend.common.implement;

import static com.moyo.backend.common.constant.MoyoConstants.GITHUB_REGISTRATION_ID;

import lombok.RequiredArgsConstructor;

import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.stereotype.Component;

/**
 *  서버측 Storage에 저장된 사용자의 Github OAuth Token이 필요할 때 사용
 *
 *  OAuth2 Client에서 제공하는 OAuth2AuthorizedClientService는 비즈니스 레이어가 아님. 구현 레이어로 취급 했음.
 */

@Component
@RequiredArgsConstructor
public class GithubOAuthTokenReader {

	private final OAuth2AuthorizedClientService oAuth2AuthorizedClientService;

	public String getGithubAccessToken(Long userId) {
		return oAuth2AuthorizedClientService.loadAuthorizedClient(GITHUB_REGISTRATION_ID, userId.toString()).getAccessToken().getTokenValue();
	}
}

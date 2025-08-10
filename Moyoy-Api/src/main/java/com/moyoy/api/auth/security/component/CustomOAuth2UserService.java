package com.moyoy.api.auth.security.component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import com.moyoy.api.auth.security.dto.GithubOAuth2User;
import com.moyoy.domain.common.github.GithubUserProfileDto;
import com.moyoy.domain.common.github.GithubUserSynchronizer;
import com.moyoy.domain.user.implement.User;

/**
 *  우리 서비스의 성격이 깃허브와 깃허브 사용자 간의 미들웨어 느낌의 서비스인 점,
 *  다른 OAuth로 사용자가 인증하더라도 우리가 제공하는대부분의 API에 Github API가 필요하여 Github OAuth를 위한 별도의 재인증이 필요한 점,
 *  이쪽에 많은 리소스를 쏟을 수 있는 상황이 아니고, Github 외에 OAuth를 사용할 여지가 없는 점을 고려해서
 *
 *  OAuth Provider == Github로 생각하고 확장성을 고려 하지 않고 트레이드 오프함.
 *
 *  OAuth Resource Server로 부터 실제 사용자의 리소스를 받아온 후, 데이터 가공 후
 *  Spring Security 에서 필요로 하는 OAuthUser를 반환하는 역할
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

	private final GithubUserSynchronizer githubUserSynchronizer;

	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

		/// @return DefaultOAuth2UserService의 loadUser 반환 값인 DefaultOAuth2User
		OAuth2User oAuth2User = super.loadUser(userRequest);
		GithubUserProfileDto githubUserProfileDto = GithubUserProfileDto.from(oAuth2User);

		User moyoyUser = githubUserSynchronizer.syncOrSignUp(githubUserProfileDto);

		return GithubOAuth2User.from(moyoyUser);
	}
}

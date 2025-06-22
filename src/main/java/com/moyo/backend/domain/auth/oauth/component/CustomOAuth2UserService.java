package com.moyo.backend.domain.auth.oauth.component;

import static com.moyo.backend.common.constant.MoyoConstants.*;

import java.util.Optional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import com.moyo.backend.domain.auth.oauth.dto.GithubOAuth2User;
import com.moyo.backend.domain.user.implement.User;
import com.moyo.backend.domain.user.implement.UserReader;
import com.moyo.backend.domain.user.implement.UserUpdater;

/**
 *  우리 서비스의 성격이 깃허브와 깃허브 사용자 간의 미들웨어 느낌의 서비스인 점,
 *  다른 OAUth로 사용자가 인증하더라도 우리가 제공하는대부분의 API에 Github API가 필요하여 Github로 재인증이 필요한 점,
 *  이쪽에 많은 리소스를 쏟을 수 있는 상황이 아니고,
 *  Github 외에 OAuth를 사용할 여지가 없는 점을 고려해서
 *
 *  OAuth Provider == Github로 생각하고 확장성을 고려 하지 않고 트레이드 오프함.
 *
 *  OAuth Resource Server로 부터 실제 사용자의 리소스를 받아온 후, 데이터 후처리 후
 *  Spring Security 에서 필요로 하는 OAuthUser를 반환하는 역할
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

	private final UserReader userReader;
	private final UserUpdater userUpdater;

	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

		// DefaultOAuth2UserService의 loadUser 반환 값인 DefaultOAuth2User 구현체 반환
		OAuth2User githubUser = super.loadUser(userRequest);

		// Github id는 Integer -> 우리는 Long 으로 사용
		Long githubUserId = Long.parseLong(githubUser.getAttribute(GITHUB_OAUTH2_USER_ID).toString());
		Optional<User> moyoUser = userReader.findById(githubUserId);

		boolean isMoyoUser = moyoUser.isPresent();

		if (isMoyoUser) {

			log.info("이미 가입된 회원 Username, ProfileImgUrl 갱신, UserId : {}", githubUser.getAttribute(GITHUB_OAUTH2_USER_ID).toString());
			userUpdater.updateProfile(moyoUser.get(), githubUser);
			return GithubOAuth2User.from(moyoUser.get());
		} else {

			log.info("신규 회원 회원 가입 진행, UserId : {}", githubUser.getAttribute(GITHUB_OAUTH2_USER_ID).toString());
			userUpdater.signUp(githubUser);
			return GithubOAuth2User.from((DefaultOAuth2User)githubUser);
		}
	}
}

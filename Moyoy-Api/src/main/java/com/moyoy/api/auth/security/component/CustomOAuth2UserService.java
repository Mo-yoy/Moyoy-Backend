package com.moyoy.api.auth.security.component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import com.moyoy.api.auth.security.principal.GithubOAuth2User;
import com.moyoy.api.user.application.UserService;
import com.moyoy.api.user.application.request.UserSyncData;
import com.moyoy.api.user.application.response.UserSyncResult;

/**
 *  <p>우리 서비스의 성격이 깃허브와 깃허브 사용자 간의 미들웨어 느낌의 서비스인 점,
 *  다른 OAuth로 사용자가 인증하더라도 우리가 제공하는대부분의 API에 Github API가 필요하여 Github OAuth를 위한 별도의 재인증이 필요한 점,
 *  이쪽에 많은 리소스를 쏟을 수 있는 상황이 아니고, Github 외에 OAuth를 사용할 여지가 없는 점을 고려해서 OAuth Provider == Github로 생각하고 확장성을 고려 하지 않고 트레이드 오프함.</p>
 *
 *  <p>
 *  Resource Server에 유저 정보 요청 후, 응답(JSON)을 OAuth2User 객체로 매핑해서 Spring Security 필터 체인에 넘겨주는 역할
 *  <br/><br/>
 *
 *  회원가입, 프로필 갱신 같은 이벤트를 중간에 낀다면 UserService를 호출하는 Controller 처럼 이용하면 될듯함.
 *  </p>
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

	private final UserService userService;

	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

		OAuth2User oAuth2User = super.loadUser(userRequest);

		UserSyncData data = UserSyncData.from(oAuth2User);
		UserSyncResult result = userService.syncOrSignUp(data);

		return GithubOAuth2User.from(result);
	}
}

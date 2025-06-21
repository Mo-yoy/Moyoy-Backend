package com.moyo.backend.domain.security.oauth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.dao.DataAccessException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import com.moyo.backend.domain.user.domain.Role;
import com.moyo.backend.domain.user.domain.User;
import com.moyo.backend.domain.user.domain.UserRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

	private final UserRepository userRepository;

	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

		try {
			// DefaultOAuth2User
			OAuth2User oAuth2User = super.loadUser(userRequest);

			// Github id는 Integer
			User user = userRepository.findById(Long.parseLong(oAuth2User.getAttribute("id").toString())).orElse(null);

			if (user == null)
				signUp(oAuth2User);
			else
				updateProfile(user, oAuth2User);

			return GithubOAuth2UserConverter.convert(oAuth2User, user);

		} catch (DataAccessException dbEx) {
			OAuth2Error oauth2Error = new OAuth2Error("db_error", "DB 에러 발생: " + dbEx.getMessage(), null);
			throw new OAuth2AuthenticationException(oauth2Error, dbEx);
		} catch (Exception ex) {
			OAuth2Error oauth2Error = new OAuth2Error("unknown_error", "인증 처리 중 알 수 없는 에러 발생: " + ex.getMessage(), null);
			throw new OAuth2AuthenticationException(oauth2Error, ex);
		}
	}

	// 이 부분 트랜잭션은 추후 처리함
	private void signUp(OAuth2User oAuth2User) {

		log.info("신규 회원 회원 가입 진행, UserId : {}", oAuth2User.getAttribute("id").toString());
		User user = User.from(oAuth2User);
		user.changeRole(Role.USER);
		userRepository.save(user);
	}

	private void updateProfile(User user, OAuth2User oAuth2User) {

		log.info("이미 가입된 회원 Username, ProfileImgUrl 갱신, UserId : {}", oAuth2User.getAttribute("id").toString());
		user.changeUsername(oAuth2User.getAttribute("login"));
		user.changeProfileImgUrl(oAuth2User.getAttribute("avatar_url"));
	}

}

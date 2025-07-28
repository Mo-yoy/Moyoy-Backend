package com.moyo.backend.domain.auth.oauth.component;

import java.util.Optional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.moyo.backend.domain.auth.oauth.dto.GithubUserProfileDto;
import com.moyo.backend.domain.user.implement.User;
import com.moyo.backend.domain.user.implement.UserCreator;
import com.moyo.backend.domain.user.implement.UserReader;

@Slf4j
@Component
@RequiredArgsConstructor
public class GithubUserSynchronizer {

	private final UserCreator userCreator;
	private final UserReader userReader;

	@Transactional
	public User syncOrSignUp(GithubUserProfileDto githubUserProfileDto) {

		Optional<User> moyoyUser = userReader.findByGithubUserId(githubUserProfileDto.githubUserId());
		boolean isExistingUser = moyoyUser.isPresent();

		if (isExistingUser) {

			User user = moyoyUser.get();
			user.changeUsername(githubUserProfileDto.username());
			user.changeProfileImgUrl(githubUserProfileDto.profileImgUrl());
			log.info("기존 회원 Github 프로필 업데이트, UserId : {}, Github User Id : {}", moyoyUser.get().getId(), githubUserProfileDto.githubUserId());
			return user;
		} else {

			User newUser = userCreator.signUp(githubUserProfileDto);
			log.info("신규 회원 회원 가입 진행, UserId : {}, Github User Id : {}", newUser.getId(), githubUserProfileDto.githubUserId());
			return newUser;
		}
	}

}

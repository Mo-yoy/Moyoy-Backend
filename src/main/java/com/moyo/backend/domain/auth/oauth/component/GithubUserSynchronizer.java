package com.moyo.backend.domain.auth.oauth.component;

import java.util.Optional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.moyo.backend.domain.auth.oauth.dto.GithubUserDto;
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
	public User syncOrSignUp(GithubUserDto githubUserDto) {

		Optional<User> moyoyUser = userReader.findByGithubUserId(githubUserDto.githubUserId());
		boolean isExistingUser = moyoyUser.isPresent();

		if (isExistingUser) {

			User user = moyoyUser.get();
			user.changeUsername(githubUserDto.username());
			user.changeProfileImgUrl(githubUserDto.profileImgUrl());
			log.info("기존 회원 Github 프로필 업데이트, UserId : {}, Github User Id : {}", moyoyUser.get().getId(), githubUserDto.githubUserId());
			return user;
		} else {

			User newUser = userCreator.signUp(githubUserDto);
			log.info("신규 회원 회원 가입 진행, UserId : {}, Github User Id : {}", newUser.getId(), githubUserDto.githubUserId());
			return newUser;
		}
	}

}

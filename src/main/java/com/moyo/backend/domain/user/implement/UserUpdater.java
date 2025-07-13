package com.moyo.backend.domain.user.implement;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import com.moyo.backend.domain.auth.oauth.dto.GithubUserDto;
import com.moyo.backend.domain.user.data_access.UserRepository;

import jakarta.transaction.Transactional;

@Component
@Transactional
@RequiredArgsConstructor
public class UserUpdater {

	private final UserRepository userRepository;

	/**
	 * 해당 메서드를 호출하는 User Read가 포함된 트랜잭션과 별도의 트랜잭션임
	 *
	 * @param moyoUser : 준영속 엔티티, 누락 값이 있으면 안됨.
	 * @param githubUserDto
	 */
	public void updateProfile(User moyoUser, GithubUserDto githubUserDto) {

		moyoUser.changeUsername(githubUserDto.username());
		moyoUser.changeProfileImgUrl(githubUserDto.profileImgUrl());
		userRepository.save(moyoUser);
	}
}

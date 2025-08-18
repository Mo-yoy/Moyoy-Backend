package com.moyoy.api.user.application;

import java.util.Optional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.moyoy.api.user.application.request.UserSyncData;

import com.moyoy.api.user.application.response.UserSearchResult;

import com.moyoy.api.user.application.response.UserSyncResult;
import com.moyoy.domain.ranking.Ranking;
import com.moyoy.domain.ranking.RankingRepository;
import com.moyoy.domain.support.error.ranking.RankingNotFoundException;
import com.moyoy.domain.support.error.user.UserNotFoundException;
import com.moyoy.domain.user.User;
import com.moyoy.domain.user.UserCreate;
import com.moyoy.domain.user.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final RankingRepository rankingRepository;

	public UserSearchResult getUserProfile(Long userId) {

		User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
		Ranking ranking = rankingRepository.findById(userId).orElseThrow(RankingNotFoundException::new);

		return UserSearchResult.from(user, ranking);
	}

	@Transactional
	public UserSyncResult syncOrSignUp(UserSyncData userSyncData) {

		Integer githubUserId = userSyncData.githubUserId();

		Optional<User> foundUser = userRepository.findByGithubUserId(githubUserId);

		return foundUser
			.map(user -> updateProfile(user, userSyncData))
			.orElseGet(() -> signUp(userSyncData));
	}

	private UserSyncResult updateProfile(User user, UserSyncData data) {

		user.changeProfile(data.userTag(), data.profileImgUrl());
		userRepository.save(user);
		log.info("기존 회원 GitHub 프로필 업데이트, userId={}, githubUserId={}", user.getId(), data.githubUserId());
		return UserSyncResult.from(user);
	}

	private UserSyncResult signUp(UserSyncData data) {

		UserCreate create = UserCreate.of(data.githubUserId(), data.userTag(), data.profileImgUrl());
		User newUser = userRepository.save(User.from(create));
		rankingRepository.save(Ranking.createInitial(newUser.getId()));
		log.info("신규 회원 가입, userId={}, githubUserId={}", newUser.getId(), data.githubUserId());
		return UserSyncResult.from(newUser);
	}


}

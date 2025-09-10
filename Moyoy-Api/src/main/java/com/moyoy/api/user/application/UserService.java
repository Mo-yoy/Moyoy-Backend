package com.moyoy.api.user.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.moyoy.api.user.application.request.UserSyncData;
import com.moyoy.api.user.application.response.UserProfileQueryResult;
import com.moyoy.api.user.application.response.UserSyncResult;

import com.moyoy.domain.ranking.Ranking;
import com.moyoy.domain.ranking.RankingRepository;
import com.moyoy.domain.user.SocialSize;
import com.moyoy.domain.user.User;
import com.moyoy.domain.user.UserRepository;
import com.moyoy.domain.user.dto.UserCreate;
import com.moyoy.domain.user.error.UserGithubAccountTypeNotAllowException;
import com.moyoy.domain.user.error.UserNotFoundException;

import com.moyoy.infra.database.mysql.query.dto.UserRankingView;
import com.moyoy.infra.database.mysql.query.port.UserRankingReader;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final RankingRepository rankingRepository;
	private final UserRankingReader userRankingReader;

	///  도메인 모델이 필요없는 단순 조회
	public UserProfileQueryResult getUserProfile(Long userId) {

		UserRankingView userRankingView = userRankingReader.findByUserId(userId).orElseThrow(UserNotFoundException::new);

		return UserProfileQueryResult.from(userRankingView);
	}

	@Transactional
	public UserSyncResult syncOrSignUp(UserSyncData userSyncData) {

		Integer githubUserId = userSyncData.githubUserId();

		return userRepository.findByGithubUserId(githubUserId)
			.map(user -> sync(user, userSyncData))
			.orElseGet(() -> signUp(userSyncData));
	}

	private UserSyncResult sync(User user, UserSyncData data) {

		SocialSize socialSize = SocialSize.of(data.followers(), data.following());

		user.changeSocialSize(socialSize);
		user.changeProfile(data.username(), data.profileImgUrl());
		userRepository.save(user);

		log.info("기존 회원 GitHub 프로필 업데이트, userId={}", user.getId());

		return UserSyncResult.from(user);
	}

	private UserSyncResult signUp(UserSyncData data) {

		if (!data.type().equals("User"))
			throw new UserGithubAccountTypeNotAllowException();

		SocialSize socialSize = SocialSize.of(data.followers(), data.following());

		UserCreate create = UserCreate.of(data.githubUserId(), data.username(), data.profileImgUrl(), socialSize);
		User newUser = User.from(create);
		newUser = userRepository.save(newUser);

		Ranking ranking = Ranking.createInitial(newUser.getId());
		rankingRepository.save(ranking);

		log.info("신규 회원 가입, userId={}, githubUserId={}, socialSize={}", newUser.getId(), newUser.getGithubUserId(), newUser.getSocialSize());

		return UserSyncResult.from(newUser);
	}
}

package com.moyoy.api.user.application;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import com.moyoy.api.user.application.request.GithubUserProfileDto;
import com.moyoy.api.user.application.response.UserProfileResult;
import com.moyoy.domain.ranking.Ranking;
import com.moyoy.domain.ranking.RankingRepository;
import com.moyoy.domain.support.error.ranking.RankingNotFoundException;
import com.moyoy.domain.support.error.user.UserNotFoundException;
import com.moyoy.domain.user.User;
import com.moyoy.domain.user.UserCreate;
import com.moyoy.domain.user.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final RankingRepository rankingRepository;

	public UserProfileResult getUserProfile(Long userId) {

		User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
		Ranking ranking = rankingRepository.findById(userId).orElseThrow(RankingNotFoundException::new);

		return UserProfileResult.from(user, ranking);
	}

	public User signUp(GithubUserProfileDto githubUserProfileDto) {

		UserCreate userCreate = new UserCreate(githubUserProfileDto.githubUserId(), githubUserProfileDto.username(), githubUserProfileDto.profileImgUrl());
		User user = User.from(userCreate);
		userRepository.save(user);

		Ranking ranking = Ranking.createInitial(user.getId());
		rankingRepository.save(ranking);

		return user;
	}
}

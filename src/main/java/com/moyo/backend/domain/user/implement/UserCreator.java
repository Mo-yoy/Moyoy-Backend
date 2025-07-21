package com.moyo.backend.domain.user.implement;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import com.moyo.backend.domain.auth.oauth.dto.GithubUserProfileDto;
import com.moyo.backend.domain.github_ranking.data_access.RankingRepository;
import com.moyo.backend.domain.github_ranking.implement.Ranking;
import com.moyo.backend.domain.user.data_access.UserRepository;

@Component
@RequiredArgsConstructor
public class UserCreator {

	private final UserRepository userRepository;
	private final RankingRepository rankingRepository;

	public User signUp(GithubUserProfileDto githubUserProfileDto) {

		User user = User.from(githubUserProfileDto);
		user.initRole();
		userRepository.save(user);

		Ranking ranking = Ranking.initRanking(user.getId());
		rankingRepository.save(ranking);

		return user;
	}
}

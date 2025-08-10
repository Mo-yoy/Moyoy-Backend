package com.moyoy.domain.user.implement;

import org.springframework.stereotype.Component;

import com.moyoy.domain.common.github.GithubUserProfileDto;
import com.moyoy.domain.ranking.data_access.RankingRepository;
import com.moyoy.domain.ranking.implement.Ranking;
import com.moyoy.domain.user.data_access.UserRepository;

import lombok.RequiredArgsConstructor;

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

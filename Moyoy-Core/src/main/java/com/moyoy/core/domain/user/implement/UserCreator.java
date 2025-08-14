package com.moyoy.core.domain.user.implement;

import org.springframework.stereotype.Component;

import com.moyoy.core.domain.ranking.implement.RankingMapper;
import com.moyoy.infra.database.domain.ranking.RankingEntity;
import com.moyoy.infra.database.domain.ranking.RankingRepository;
import com.moyoy.core.domain.ranking.implement.Ranking;
import com.moyoy.infra.database.domain.user.UserEntity;
import com.moyoy.infra.database.domain.user.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserCreator {

	private final UserRepository userRepository;
	private final RankingRepository rankingRepository;

	public User signUp(GithubUserProfileDto githubUserProfileDto) {

		User user = User.from(githubUserProfileDto);
		user.initRole();

		UserEntity userEntity = UserMapper.toEntity(user);
		userRepository.save(userEntity);

		Ranking ranking = Ranking.initRanking(user.getId());
		RankingEntity rankingEntity = RankingMapper.toEntity(ranking);
		rankingRepository.save(rankingEntity);

		return user;
	}
}

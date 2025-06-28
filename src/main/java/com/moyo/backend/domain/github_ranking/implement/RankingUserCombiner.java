package com.moyo.backend.domain.github_ranking.implement;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import com.moyo.backend.domain.user.implement.User;

@Component
@RequiredArgsConstructor
public class RankingUserCombiner {

	public List<RankingWithUser> combine(List<User> users, List<Ranking> rankings) {
		Map<Long, User> userMap = getUserMap(users);

		return rankings.stream()
			.map(ranking -> {
				User user = userMap.get(ranking.getUserId());
				return new RankingWithUser(ranking, user);
			})
			.toList();
	}

	private Map<Long, User> getUserMap(List<User> users) {
		return users.stream()
			.collect(Collectors.toMap(User::getId, user -> user));
	}
}
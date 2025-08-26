package com.moyoy.api.ranking.application;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import com.moyoy.api.ranking.application.response.RankingUserView;

import com.moyoy.domain.ranking.Ranking;
import com.moyoy.domain.user.User;

@Component
@RequiredArgsConstructor
public class RankingUserCombiner {

	public List<RankingUserView> combine(List<User> users, List<Ranking> rankings) {
		Map<Long, User> userMap = getUserMap(users);

		return rankings.stream()
			.map(ranking -> {
				User user = userMap.get(ranking.getUserId());
				return new RankingUserView(ranking, user);
			})
			.toList();
	}

	private Map<Long, User> getUserMap(List<User> users) {
		return users.stream()
			.collect(Collectors.toMap(User::getId, user -> user));
	}
}

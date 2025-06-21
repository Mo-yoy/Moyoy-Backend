package com.moyo.backend.domain.github_ranking.presentation.dto;

import java.util.List;

import org.springframework.data.domain.Slice;

import com.moyo.backend.domain.github_ranking.implement.RankingDuration;
import com.moyo.backend.domain.user.domain.User;

public record RankingSearchResponse(
	List<UserRankingDetails> userList,
	boolean lastPage) {

	public static RankingSearchResponse from(Slice<User> users, RankingDuration duration) {

		List<UserRankingDetails> userList = users.getContent().stream()
			.map(user -> UserRankingDetails.from(user, duration))
			.toList();

		return new RankingSearchResponse(userList, users.isLast());
	}
}

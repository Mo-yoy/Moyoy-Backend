package com.moyo.backend.domain.github_ranking.business;

import java.util.List;

import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import com.moyo.backend.domain.github_ranking.implement.RankingDuration;
import com.moyo.backend.domain.user.implement.User;

public record RankingSearchResult(Slice<UserRankingDetails> userRankingDetailsSlice) {

	public static RankingSearchResult from(Slice<User> users, RankingDuration duration) {

		List<UserRankingDetails> userList = users.getContent().stream()
			.map(user -> UserRankingDetails.from(user, duration))
			.toList();

		SliceImpl<UserRankingDetails> userRankingDetailsSlice = new SliceImpl<>(userList, users.getPageable(), users.hasNext());

		return new RankingSearchResult(userRankingDetailsSlice);
	}
}

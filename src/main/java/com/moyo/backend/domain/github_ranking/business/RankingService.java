package com.moyo.backend.domain.github_ranking.business;

import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import com.moyo.backend.domain.github_ranking.implement.Ranking;
import com.moyo.backend.domain.github_ranking.implement.RankingPeriod;
import com.moyo.backend.domain.github_ranking.implement.RankingReader;
import com.moyo.backend.domain.github_ranking.implement.RankingSlice;
import com.moyo.backend.domain.github_ranking.implement.RankingUserCombiner;
import com.moyo.backend.domain.github_ranking.implement.RankingWithUser;
import com.moyo.backend.domain.user.implement.User;
import com.moyo.backend.domain.user.implement.UserReader;

@Service
@RequiredArgsConstructor
public class RankingService {

	private final UserReader userReader;
	private final RankingReader rankingReader;
	private final RankingUserCombiner rankingUserCombiner;

	public RankingSearchResult searchAllUserRanking(RankingSearch rankingSearch) {

		RankingSlice rankingSlice = rankingReader.getAllRanking(rankingSearch.period(), rankingSearch.page(), rankingSearch.size());
		List<Ranking> rankings = rankingSlice.rankingList();

		List<Long> userIds = extractUserIds(rankings);
		List<User> users = userReader.findAllById(userIds);

		List<RankingWithUser> rankingWithUsers = rankingUserCombiner.combine(users, rankings);

		return new RankingSearchResult(rankingWithUsers, rankingSlice.isLast());
	}

	private List<Long> extractUserIds(List<Ranking> rankings) {
		return rankings.stream()
			.map(Ranking::getUserId)
			.toList();
	}
}

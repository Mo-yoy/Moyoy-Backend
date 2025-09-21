package com.moyoy.api.ranking.application;

import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import com.moyoy.api.ranking.application.request.RankingSearch;
import com.moyoy.api.ranking.application.response.RankingSearchResult;
import com.moyoy.api.ranking.application.response.RankingUserView;
import com.moyoy.domain.ranking.Ranking;
import com.moyoy.domain.ranking.RankingRepository;
import com.moyoy.common.page.PageData;
import com.moyoy.common.page.SliceResult;
import com.moyoy.domain.user.User;
import com.moyoy.domain.user.UserRepository;

@Service
@RequiredArgsConstructor
public class RankingService {

	private final UserRepository userRepository;
	private final RankingRepository rankingRepository;
	private final RankingUserCombiner rankingUserCombiner;

	public RankingSearchResult searchAllUserRanking(RankingSearch rankingSearch) {

		PageData pageData = PageData.of(rankingSearch.page(), rankingSearch.size());
		SliceResult<Ranking> rankingSlice = rankingRepository.findAll(rankingSearch.period(), pageData);
		List<Ranking> rankings = rankingSlice.content();

		List<Long> userIds = extractUserIds(rankings);
		List<User> users = userRepository.findByIdIn(userIds);

		List<RankingUserView> rankingUserViews = rankingUserCombiner.combine(users, rankings);

		return new RankingSearchResult(rankingUserViews, rankingSlice.isLast());
	}

	private List<Long> extractUserIds(List<Ranking> rankings) {
		return rankings.stream()
			.map(Ranking::getUserId)
			.toList();
	}
}

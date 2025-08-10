package com.moyoy.api.github_ranking.business;

import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import com.moyoy.domain.follow.GithubFollowRelation;
import com.moyoy.domain.follow.GithubFollowRelationReader;
import com.moyoy.domain.ranking.implement.Ranking;
import com.moyoy.domain.ranking.implement.RankingReader;
import com.moyoy.domain.ranking.implement.RankingSlice;
import com.moyoy.domain.ranking.implement.RankingUserCombiner;
import com.moyoy.domain.ranking.implement.RankingWithUser;
import com.moyoy.domain.user.implement.User;
import com.moyoy.domain.user.implement.UserReader;

@Service
@RequiredArgsConstructor
public class RankingService {

	private final UserReader userReader;
	private final RankingReader rankingReader;
	private final RankingUserCombiner rankingUserCombiner;
	private final GithubFollowRelationReader githubFollowRelationReader;

	public RankingSearchResult searchAllUserRanking(RankingSearch rankingSearch) {

		RankingSlice rankingSlice = rankingReader.getAllRanking(rankingSearch.period(), rankingSearch.page(), rankingSearch.size());
		List<Ranking> rankings = rankingSlice.rankingList();

		List<Long> userIds = extractUserIds(rankings);
		List<User> users = userReader.findAllById(userIds);

		List<RankingWithUser> rankingWithUsers = rankingUserCombiner.combine(users, rankings);

		return new RankingSearchResult(rankingWithUsers, rankingSlice.isLast());
	}

	public RankingSearchResult searchUserFollowingsRanking(Long userId, RankingSearch rankingSearch) {

		GithubFollowRelation githubFollowRelation = githubFollowRelationReader.findByUserId(userId, false);
		List<Integer> followingUserGithubIdList = githubFollowRelation.getGithubFollowingUserIds();

		RankingSlice rankingSlice = rankingReader.getFollowingsRanking(followingUserGithubIdList, rankingSearch.period(), rankingSearch.page(), rankingSearch.size());
		List<Ranking> rankings = rankingSlice.rankingList();

		List<User> users = userReader.findAllById(extractUserIds(rankings));
		List<RankingWithUser> rankingWithUsers = rankingUserCombiner.combine(users, rankings);

		return new RankingSearchResult(rankingWithUsers, rankingSlice.isLast());
	}

	private List<Long> extractUserIds(List<Ranking> rankings) {
		return rankings.stream()
			.map(Ranking::getUserId)
			.toList();
	}
}

package com.moyo.backend.domain.batchLegacy.ranking.implement;

import static com.moyo.backend.common.constant.MoyoConstants.*;

import com.moyo.backend.common.exception.github_follow.GithubRateLimitExceedException;
import com.moyo.backend.domain.batchLegacy.ranking.data_access.UserCountAndLastId;

public record UserRankingBatchSnapshot(
	int userCount,
	Long lastUserId) {

	public static UserRankingBatchSnapshot from(UserCountAndLastId userCountAndLastId) {
		return new UserRankingBatchSnapshot(userCountAndLastId.userCount(), userCountAndLastId.lastUserId());
	}

	public static record RankingPreflight(
		String username,
		int followerCount,
		int remainingRequestCount) {

		// 배치 작업에 정확히 몇번 요청이 필요할지 알 수 없음
		public void assertCanGithubRequest() {
			if (remainingRequestCount < GITHUB_MIN_REQUEST_THRESHOLD)
				throw new GithubRateLimitExceedException();
		}
	}
}

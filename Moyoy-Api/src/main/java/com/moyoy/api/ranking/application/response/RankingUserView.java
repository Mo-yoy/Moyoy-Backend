package com.moyoy.api.ranking.application.response;

import com.moyoy.domain.ranking.Ranking;
import com.moyoy.domain.user.User;

public record RankingUserView(
	Ranking ranking,
	User user) {
}

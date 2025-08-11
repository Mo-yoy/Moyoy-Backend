package com.moyoy.core.domain.ranking.implement;

import com.moyoy.core.domain.user.implement.User;

public record RankingWithUser(
	Ranking ranking,
	User user) {

}

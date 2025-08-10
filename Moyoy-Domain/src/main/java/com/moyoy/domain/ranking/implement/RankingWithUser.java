package com.moyoy.domain.ranking.implement;

import com.moyoy.domain.user.implement.User;

public record RankingWithUser(
	Ranking ranking,
	User user) {

}

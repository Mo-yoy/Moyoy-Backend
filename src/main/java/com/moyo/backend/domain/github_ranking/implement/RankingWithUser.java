package com.moyo.backend.domain.github_ranking.implement;

import com.moyo.backend.domain.user.implement.User;

public record RankingWithUser(
	Ranking ranking,
	User user) {

}

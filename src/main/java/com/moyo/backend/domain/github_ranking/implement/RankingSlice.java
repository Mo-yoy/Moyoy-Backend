package com.moyo.backend.domain.github_ranking.implement;

import java.util.List;

public record RankingSlice(
	List<Ranking> rankingList,
	boolean isLast) {

}

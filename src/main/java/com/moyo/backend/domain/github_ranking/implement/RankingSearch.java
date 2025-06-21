package com.moyo.backend.domain.github_ranking.implement;

public record RankingSearch(
	RankingDuration duration,
	int page,
	int size) {

	public RankingSearch(
		String duration,
		int page,
		int size) {
		this(RankingDuration.valueOf(duration), page, size);
	}
}

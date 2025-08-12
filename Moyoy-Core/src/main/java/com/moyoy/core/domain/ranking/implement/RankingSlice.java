package com.moyoy.core.domain.ranking.implement;

import java.util.List;


public record RankingSlice(
	List<Ranking> rankingList,
	boolean isLast) {

}

package com.moyoy.core.domain.ranking.implement;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;

import com.moyoy.core.support.error.ranking.RankingNotFoundException;
import com.moyoy.infra.database.domain.ranking.RankingEntity;
import com.moyoy.infra.database.domain.ranking.RankingRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RankingReader {

	private final RankingRepository rankingRepository;

	public Ranking getRanking(Long userId) {

		RankingEntity rankingEntity = rankingRepository.findById(userId).orElseThrow(RankingNotFoundException::new);

		return RankingMapper.toModel(rankingEntity);
	}

	public RankingSlice getAllRanking(RankingPeriod rankingPeriod, int page, int size) {

		Pageable pageable = PageRequest.of(page, size);
		Slice<RankingEntity> rankingSlice = rankingRepository.findAll(rankingPeriod.getValue(), pageable);

		List<Ranking> rankingList = rankingSlice.getContent()
			.stream()
			.map(RankingMapper::toModel)
			.toList();

		return new RankingSlice(rankingList, rankingSlice.isLast());
	}

	public RankingSlice getFollowingsRanking(List<Integer> followingUserIds, RankingPeriod rankingPeriod, int page, int size) {

		Pageable pageable = PageRequest.of(page, size);
		Slice<RankingEntity> rankingSlice = rankingRepository.findFollowingUserRankings(followingUserIds, rankingPeriod.getValue(), pageable);

		List<Ranking> rankingList = rankingSlice.getContent()
			.stream()
			.map(RankingMapper::toModel)
			.toList();

		return new RankingSlice(rankingList, rankingSlice.hasNext());
	}
}

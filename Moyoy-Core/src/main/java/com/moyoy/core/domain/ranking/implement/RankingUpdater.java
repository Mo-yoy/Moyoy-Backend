package com.moyoy.core.domain.ranking.implement;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.moyoy.infra.database.domain.ranking.RankingEntity;
import com.moyoy.infra.database.domain.ranking.RankingRepository;

import lombok.RequiredArgsConstructor;

@Component
@Transactional
@RequiredArgsConstructor
public class RankingUpdater {

	private final RankingRepository rankingRepository;

	public void update(Ranking ranking) {

		RankingEntity rankingEntity = RankingMapper.toEntity(ranking);
		rankingRepository.save(rankingEntity);
	}

	public void updateAll(List<Ranking> updatedRankings) {

		List<RankingEntity> rankingEntityList = updatedRankings.stream().map(RankingMapper::toEntity).toList();
		rankingRepository.saveAll(rankingEntityList);
	}
}

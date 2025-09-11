package com.moyoy.infra.database.mysql.ranking;

import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;

import com.moyoy.domain.ranking.Ranking;
import com.moyoy.domain.ranking.RankingPeriod;
import com.moyoy.domain.ranking.RankingRepository;

import com.moyoy.common.page.PageData;
import com.moyoy.common.page.SliceResult;

@Component
@RequiredArgsConstructor
public class RankingRepositoryImpl implements RankingRepository {

	private final RankingJpaRepository rankingJpaRepository;

	@Override
	public Optional<Ranking> findById(Long id) {

		Optional<RankingEntity> rankingEntity = rankingJpaRepository.findById(id);
		return rankingEntity.map(RankingMapper::toModel);
	}

	@Override
	public Optional<Ranking> findByUserId(Long userId) {

		Optional<RankingEntity> rankingEntity = rankingJpaRepository.findByUserId(userId);
		return rankingEntity.map(RankingMapper::toModel);
	}

	@Override
	public void save(Ranking ranking) {

		RankingEntity rankingEntity = RankingMapper.toEntity(ranking);
		rankingJpaRepository.save(rankingEntity);
	}

}

package com.moyoy.infra.database.mysql.ranking;

import java.util.Optional;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import com.moyoy.domain.ranking.Ranking;
import com.moyoy.domain.ranking.RankingRepository;

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

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
import com.moyoy.domain.support.page.PageData;
import com.moyoy.domain.support.page.SliceResult;

@Component
@RequiredArgsConstructor
public class RankingRepositoryImpl implements RankingRepository {

	private final RankingJpaRepository rankingJpaRepository;
	private final RankingQueryDslRepository rankingQueryDslRepository;

	@Override
	public Optional<Ranking> findById(Long id) {

		Optional<RankingEntity> rankingEntity = rankingJpaRepository.findById(id);
		return rankingEntity.map(RankingMapper::toModel);
	}

	@Override
	public Optional<Ranking> findByUserId(Long userId) {

		RankingEntity rankingEntity = rankingJpaRepository.findByUserId(userId);
		return Optional.ofNullable(RankingMapper.toModel(rankingEntity));
	}

	@Override
	public void save(Ranking ranking) {

		RankingEntity rankingEntity = RankingMapper.toEntity(ranking);
		rankingJpaRepository.save(rankingEntity);
	}

	@Override
	public void saveAll(List<Ranking> updatedRankings) {

		List<RankingEntity> rankingEntityList = updatedRankings.stream().map(RankingMapper::toEntity).toList();
		rankingJpaRepository.saveAll(rankingEntityList);
	}



	@Override
	public SliceResult<Ranking> findAll(RankingPeriod duration, PageData pageData) {

		Pageable pageable = PageRequest.of(pageData.page(), pageData.size());
		Slice<RankingEntity> rankingEntitySlice = rankingQueryDslRepository.findAll(duration, pageable);
		List<Ranking> rankingList = rankingEntitySlice.getContent().stream().map(RankingMapper::toModel).toList();

		return SliceResult.of(rankingList, rankingEntitySlice.hasNext());
	}

}

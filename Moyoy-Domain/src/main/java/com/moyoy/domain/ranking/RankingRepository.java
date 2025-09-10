package com.moyoy.domain.ranking;

import java.util.List;
import java.util.Optional;

import com.moyoy.common.page.PageData;
import com.moyoy.common.page.SliceResult;

public interface RankingRepository {

	Optional<Ranking> findById(Long id);

	Optional<Ranking> findByUserId(Long userId);

	void save(Ranking ranking);

	void saveAll(List<Ranking> rankings);

	SliceResult<Ranking> findAll(RankingPeriod duration, PageData pageData);
}

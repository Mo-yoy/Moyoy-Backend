package com.moyoy.domain.ranking;

import java.util.List;
import java.util.Optional;

import com.moyoy.common.page.PageData;
import com.moyoy.common.page.SliceResult;

public interface RankingRepository {

	void save(Ranking ranking);

	void saveAll(List<Ranking> rankings);

	Optional<Ranking> findById(Long userId);

	SliceResult<Ranking> findAll(RankingPeriod duration, PageData pageData);
}

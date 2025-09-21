package com.moyoy.infra.database.mysql.query;

import java.util.Optional;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import com.moyoy.domain.ranking.RankingPeriod;

import com.moyoy.infra.database.mysql.query.dto.UserProfileView;
import com.moyoy.infra.database.mysql.query.dto.UserRankingView;
import com.moyoy.infra.database.mysql.query.port.UserRankingReader;

import com.moyoy.common.page.PageData;
import com.moyoy.common.page.SliceResult;

@Component
@RequiredArgsConstructor
class UserRankingReaderImpl implements UserRankingReader {

	private final UserRankingQueryDslRepository userRankingQueryDslRepository;

	@Override
	public Optional<UserProfileView> findByUserId(Long userId) {
		return userRankingQueryDslRepository.findUserRankingView(userId);
	}

	@Override
	public SliceResult<UserRankingView> findAll(RankingPeriod period, PageData pageData) {
		return userRankingQueryDslRepository.findAll(period, pageData);
	}
}

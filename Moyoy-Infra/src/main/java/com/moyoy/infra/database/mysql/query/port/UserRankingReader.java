package com.moyoy.infra.database.mysql.query.port;

import java.util.Optional;

import com.moyoy.domain.ranking.RankingPeriod;

import com.moyoy.infra.database.mysql.query.dto.UserProfileView;
import com.moyoy.infra.database.mysql.query.dto.UserRankingView;

import com.moyoy.common.page.PageData;
import com.moyoy.common.page.SliceResult;

public interface UserRankingReader {

	Optional<UserProfileView> findByUserId(Long userId);

	SliceResult<UserRankingView> findAll(RankingPeriod period, PageData pageData);
}

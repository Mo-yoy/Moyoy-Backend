package com.moyoy.api.ranking.application;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import com.moyoy.api.ranking.application.request.RankingSearchData;
import com.moyoy.api.ranking.application.response.RankingSearchResult;

import com.moyoy.infra.database.mysql.query.dto.UserRankingView;
import com.moyoy.infra.database.mysql.query.port.UserRankingReader;

import com.moyoy.common.page.PageData;
import com.moyoy.common.page.SliceResult;

@Service
@RequiredArgsConstructor
public class RankingService {

	private final UserRankingReader userRankingReader;

	///  도메인 모델이 필요없는 단순 조회
	public RankingSearchResult searchAllUserRanking(RankingSearchData data) {

		PageData pageData = PageData.of(data.page(), data.size());
		SliceResult<UserRankingView> rankingViewSlice = userRankingReader.findAll(data.period(), pageData);

		return RankingSearchResult.from(rankingViewSlice);
	}
}

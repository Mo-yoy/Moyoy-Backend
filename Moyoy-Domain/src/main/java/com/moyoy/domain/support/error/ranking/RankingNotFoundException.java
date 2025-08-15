package com.moyoy.domain.support.error.ranking;

import static com.moyoy.domain.support.error.ranking.RankingErrorCode.*;

import com.moyoy.domain.support.error.MoyoException;

public class RankingNotFoundException extends MoyoException {
	public RankingNotFoundException() {
		super(RANKING_NOT_EXIST);
	}
}

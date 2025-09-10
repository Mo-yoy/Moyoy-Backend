package com.moyoy.domain.ranking.error;

import static com.moyoy.domain.ranking.error.RankingErrorCode.*;

import com.moyoy.common.error.MoyoException;

public class RankingNotFoundException extends MoyoException {
	public RankingNotFoundException() {
		super(RANKING_NOT_EXIST);
	}
}

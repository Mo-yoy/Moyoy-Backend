package com.moyo.backend.common.exception.ranking;

import static com.moyo.backend.common.exception.ranking.RankingErrorCode.*;

import com.moyo.backend.common.exception.MoyoException;

public class RankingNotFoundException extends MoyoException {
	public RankingNotFoundException() {
		super(RANKING_NOT_EXIST);
	}
}

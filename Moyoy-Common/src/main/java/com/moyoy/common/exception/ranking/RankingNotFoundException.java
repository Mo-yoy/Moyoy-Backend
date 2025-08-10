package com.moyoy.common.exception.ranking;


import static com.moyoy.common.exception.ranking.RankingErrorCode.*;

import com.moyoy.common.exception.MoyoException;

public class RankingNotFoundException extends MoyoException {
	public RankingNotFoundException() {
		super(RANKING_NOT_EXIST);
	}
}

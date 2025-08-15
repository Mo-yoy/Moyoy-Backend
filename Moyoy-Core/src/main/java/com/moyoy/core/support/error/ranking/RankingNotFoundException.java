package com.moyoy.core.support.error.ranking;


import static com.moyoy.core.support.error.ranking.RankingErrorCode.*;

import com.moyoy.core.support.error.MoyoException;

public class RankingNotFoundException extends MoyoException {
	public RankingNotFoundException() {
		super(RANKING_NOT_EXIST);
	}
}

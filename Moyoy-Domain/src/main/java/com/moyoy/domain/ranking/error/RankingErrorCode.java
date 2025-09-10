package com.moyoy.domain.ranking.error;

import static com.moyoy.common.constant.MoyoConstants.*;

import lombok.AllArgsConstructor;
import lombok.Getter;

import com.moyoy.common.error.BaseErrorCode;
import com.moyoy.common.error.ErrorReason;

@Getter
@AllArgsConstructor
public enum RankingErrorCode implements BaseErrorCode {

	RANKING_NOT_EXIST(NOT_FOUND, "RANKING_404_1", "사용자의 랭킹을 찾을 수 없습니다.");

	private final Integer status;
	private final String code;
	private final String message;

	@Override
	public ErrorReason getErrorReason() {
		return new ErrorReason(status, code, message);
	}
}

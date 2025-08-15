package com.moyoy.domain.support.error.ranking;

import static com.moyoy.common.constant.MoyoConstants.*;

import lombok.AllArgsConstructor;
import lombok.Getter;

import com.moyoy.domain.support.error.BaseErrorCode;
import com.moyoy.domain.support.error.ErrorReason;

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

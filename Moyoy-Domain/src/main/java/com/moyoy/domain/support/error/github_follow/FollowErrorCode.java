package com.moyoy.domain.support.error.github_follow;

import static com.moyoy.common.constant.MoyoConstants.*;

import lombok.AllArgsConstructor;
import lombok.Getter;

import com.moyoy.common.constant.MoyoConstants;
import com.moyoy.domain.support.error.BaseErrorCode;
import com.moyoy.domain.support.error.ErrorReason;

@Getter
@AllArgsConstructor
public enum FollowErrorCode implements BaseErrorCode {

	SNAPSHOT_COOLDOWN_NOT_EXPIRED(BAD_REQUEST, "FOLLOW_400_1","최신 스냅샷 생성 후 5분 이상 경과해야 갱신할 수 있습니다.")
	;

	private final Integer status;
	private final String code;
	private final String message;

	@Override
	public ErrorReason getErrorReason() {
		return new ErrorReason(status, code, message);
	}
}

package com.moyoy.domain.github_follow.error;

import com.moyoy.common.error.MoyoException;

public class GithubFollowSnapshotCoolDownNotExpiredException extends MoyoException {
	public GithubFollowSnapshotCoolDownNotExpiredException() {
		super(FollowErrorCode.SNAPSHOT_COOLDOWN_NOT_EXPIRED);
	}
}

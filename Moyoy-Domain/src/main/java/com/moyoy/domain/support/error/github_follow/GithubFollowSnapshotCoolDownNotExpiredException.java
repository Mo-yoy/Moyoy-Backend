package com.moyoy.domain.support.error.github_follow;

import com.moyoy.domain.support.error.MoyoException;

public class GithubFollowSnapshotCoolDownNotExpiredException extends MoyoException {
	public GithubFollowSnapshotCoolDownNotExpiredException() {
		super(FollowErrorCode.SNAPSHOT_COOLDOWN_NOT_EXPIRED);
	}
}

package com.moyoy.infra.redis.cache.support.error;

import com.moyoy.common.error.MoyoException;

public class GithubFollowSnapshotCoolDownNotExpiredException extends MoyoException {
	public GithubFollowSnapshotCoolDownNotExpiredException() {
		super(CacheErrorCode.SNAPSHOT_COOLDOWN_NOT_EXPIRED);
	}
}

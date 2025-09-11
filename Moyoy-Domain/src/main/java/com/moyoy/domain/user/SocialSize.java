package com.moyoy.domain.user;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum SocialSize {

	SMALL(0),
	MEDIUM(100),
	LARGE(500),
	HUGE(1000);

	private final int threshold;

	public static SocialSize of(int followerCount, int followingCount) {

		int count = followerCount + followingCount;

		if (count >= HUGE.threshold)
			return HUGE;
		if (count >= LARGE.threshold)
			return LARGE;
		if (count >= MEDIUM.threshold)
			return MEDIUM;
		return SMALL;
	}
}

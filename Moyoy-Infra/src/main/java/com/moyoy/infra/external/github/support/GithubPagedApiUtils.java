package com.moyoy.infra.external.github.support;

import static com.moyoy.common.constant.MoyoConstants.*;

public final class GithubPagedApiUtils {

	private GithubPagedApiUtils() {}

	public static int calculateMaxPage(int totalCount) {

		return totalCount / GITHUB_MAX_QUERY_PAGING_SIZE + 1;
	}

	public static boolean isRateLimitExceeded(int apiRateRemaining, int pageSize) {

		return apiRateRemaining - pageSize >= GITHUB_MIN_REQUEST_THRESHOLD;
	}

}

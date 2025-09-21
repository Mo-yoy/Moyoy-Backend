package com.moyoy.api.follow.application.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.moyoy.api.follow.application.request.GithubFollowDetection;
import com.moyoy.domain.follow.FollowUser;
import com.moyoy.common.page.SliceResult;

public record GithubFollowDetectionResult(

	SliceResult<FollowUser> users,
	LocalDateTime lastSyncAt,
	int totalFollowUserCount) {

	public static GithubFollowDetectionResult from(List<FollowUser> users, GithubFollowDetection followDetection, LocalDateTime lastSyncAt) {

		int lastFetchedUserId = followDetection.lastGithubUserId();
		int pageSize = followDetection.size();

		List<FollowUser> filteredList = users.stream()
			.filter(user -> lastFetchedUserId == 0 || user.id() > lastFetchedUserId)
			.limit(pageSize + 1)
			.collect(Collectors.toList());

		boolean hasNext = filteredList.size() > pageSize;

		if (hasNext)
			filteredList.removeLast();

		SliceResult<FollowUser> usersSlice = new SliceResult<>(filteredList, !hasNext, hasNext);

		return new GithubFollowDetectionResult(
			usersSlice,
			lastSyncAt,
			users.size());
	}

}

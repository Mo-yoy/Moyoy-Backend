package com.moyoy.api.github_follow.business;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import com.moyoy.domain.follow.GithubFollowUser;

public record GithubFollowDetectionResult(

	Slice<GithubFollowUser> users,
	LocalDateTime lastSyncAt,
	int totalFollowUserCount) {

	public static GithubFollowDetectionResult from(List<GithubFollowUser> users, GithubFollowDetection followDetection, LocalDateTime lastSyncAt) {

		int lastFetchedUserId = followDetection.lastGithubUserId();
		int pageSize = followDetection.size();

		List<GithubFollowUser> filteredList = users.stream()
			.filter(user -> lastFetchedUserId == 0 || user.id() > lastFetchedUserId)
			.limit(pageSize + 1)
			.collect(Collectors.toList());

		boolean hasNext = filteredList.size() > pageSize;

		if (hasNext)
			filteredList.removeLast();

		SliceImpl<GithubFollowUser> usersSlice = new SliceImpl<>(filteredList, Pageable.unpaged(), hasNext);

		return new GithubFollowDetectionResult(
			usersSlice,
			lastSyncAt,
			users.size());
	}

}

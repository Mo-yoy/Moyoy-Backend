package com.moyo.backend.domain.github_follow.business;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import com.moyo.backend.domain.github_follow.implement.GithubUser;

public record GithubFollowDetectionResult(

	Slice<GithubUser> users,
	LocalDateTime lastSyncAt,
	int totalFollowUserCount) {

	public static GithubFollowDetectionResult from(List<GithubUser> users, GithubFollowDetection followDetection, LocalDateTime lastSyncAt) {

		long lastFetchedUserId = followDetection.lastUserId();
		int pageSize = followDetection.size();

		List<GithubUser> filteredList = users.stream()
			.filter(user -> lastFetchedUserId == 0 || user.id() > lastFetchedUserId)
			.limit(pageSize + 1)
			.collect(Collectors.toList());

		boolean hasNext = filteredList.size() > pageSize;

		if (hasNext)
			filteredList.removeLast();

		SliceImpl<GithubUser> usersSlice = new SliceImpl<>(filteredList, Pageable.unpaged(), hasNext);

		return new GithubFollowDetectionResult(
			usersSlice,
			lastSyncAt,
			users.size());
	}

}

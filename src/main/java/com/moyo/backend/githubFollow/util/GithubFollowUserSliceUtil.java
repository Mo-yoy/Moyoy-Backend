package com.moyo.backend.githubFollow.util;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import com.moyo.backend.githubFollow.domain.GithubFollowUser;

public class GithubFollowUserSliceUtil {

	public static Slice<GithubFollowUser> getSlice(List<GithubFollowUser> users, long lastFetchedUserId, int pageSize) {

		List<GithubFollowUser> filteredList = users.stream()
			.filter(user -> lastFetchedUserId == 0 || user.id() > lastFetchedUserId)
			.limit(pageSize + 1)
			.collect(Collectors.toList());

		boolean hasNext = filteredList.size() > pageSize;
		if (hasNext)
			filteredList.removeLast();

		return new SliceImpl<>(filteredList, Pageable.unpaged(), hasNext);
	}
}

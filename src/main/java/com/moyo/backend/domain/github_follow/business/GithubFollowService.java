package com.moyo.backend.domain.github_follow.business;

import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import com.moyo.backend.domain.github_follow.implement.GithubFollowRelation;
import com.moyo.backend.domain.github_follow.implement.GithubFollowRelationReader;
import com.moyo.backend.domain.github_follow.implement.GithubFollowUpdater;
import com.moyo.backend.domain.github_follow.implement.GithubUser;

@Service
@RequiredArgsConstructor
public class GithubFollowService {

	private final GithubFollowRelationReader githubFollowRelationReader;
	private final GithubFollowUpdater githubFollowUpdater;

	public GithubFollowDetectionResult getFollowUserSlice(Long userId, GithubFollowDetection followDetection) {

		// TODO: forceSync 검증 로직 추가
		GithubFollowRelation githubFollowRelation = githubFollowRelationReader.findByUserId(userId, followDetection.forceSync());
		List<GithubUser> filteredFollowUsers = githubFollowRelation.filterUsersByDetectType(followDetection.detectType());

		return GithubFollowDetectionResult.from(filteredFollowUsers, followDetection, githubFollowRelation.getCreatedAt());
	}

	public void follow(Long currentUserId, Long targetUserId) {

		githubFollowUpdater.follow(currentUserId, targetUserId);
	}

	public void unfollow(Long currentUserId, Long targetUserId) {

		githubFollowUpdater.unfollow(currentUserId, targetUserId);
	}
}

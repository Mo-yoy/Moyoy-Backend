package com.moyoy.api.github_follow.business;

import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import com.moyoy.domain.follow.GithubFollowRelation;
import com.moyoy.domain.follow.GithubFollowRelationReader;
import com.moyoy.domain.follow.GithubFollowUpdater;
import com.moyoy.domain.follow.GithubFollowUser;

@Service
@RequiredArgsConstructor
public class GithubFollowService {

	private final GithubFollowRelationReader githubFollowRelationReader;
	private final GithubFollowUpdater githubFollowUpdater;

	public GithubFollowDetectionResult getFollowUserSlice(Long userId, GithubFollowDetection followDetection) {

		// TODO: forceSync 검증 로직 추가
		GithubFollowRelation githubFollowRelation = githubFollowRelationReader.findByUserId(userId, followDetection.forceSync());
		List<GithubFollowUser> filteredFollowUsers = githubFollowRelation.filterUsersByDetectType(followDetection.detectType());

		return GithubFollowDetectionResult.from(filteredFollowUsers, followDetection, githubFollowRelation.getCreatedAt());
	}

	public void follow(Long currentUserId, Integer targetGithubUserId) {

		githubFollowUpdater.follow(currentUserId, targetGithubUserId);
	}

	public void unfollow(Long currentUserId, Integer targetGithubUserId) {

		githubFollowUpdater.unfollow(currentUserId, targetGithubUserId);
	}
}

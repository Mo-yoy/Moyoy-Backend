package com.moyoy.api.follow.application;

import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import com.moyoy.api.follow.application.request.GithubFollowDetection;
import com.moyoy.api.follow.application.response.GithubFollowDetectionResult;
import com.moyoy.domain.follow.FollowRelation;
import com.moyoy.domain.follow.FollowRelationRepository;
import com.moyoy.domain.follow.FollowUser;
import com.moyoy.domain.support.error.user.UserNotFoundException;
import com.moyoy.domain.user.UserRepository;
import com.moyoy.infra.external.github.common.GithubOAuthTokenReader;

@Service
@RequiredArgsConstructor
public class FollowService {

	private final GithubOAuthTokenReader githubOAuthTokenReader;
	private final UserRepository userRepository;
	private final FollowRelationRepository followRelationRepository;

	public GithubFollowDetectionResult getFollowUserSlice(Long userId, GithubFollowDetection followDetection) {

		Integer githubUserId = userRepository.findById(userId).orElseThrow(UserNotFoundException::new).getGithubUserId();
		String accessToken = githubOAuthTokenReader.getGithubAccessToken(userId);

		FollowRelation followRelation = followRelationRepository.loadFollowRelation(userId, followDetection.forceSync(), githubUserId, accessToken);
		List<FollowUser> filteredFollowUsers = followRelation.filterUsersByDetectType(followDetection.detectType());

		return GithubFollowDetectionResult.from(filteredFollowUsers, followDetection, followRelation.getCreatedAt());
	}

	public void follow(Long currentUserId, Integer targetUserGithubId) {

		Integer currentUserGithubId = userRepository.findById(currentUserId).orElseThrow(UserNotFoundException::new).getGithubUserId();
		String accessToken = githubOAuthTokenReader.getGithubAccessToken(currentUserId);

		followRelationRepository.follow(currentUserId, currentUserGithubId, targetUserGithubId, accessToken);
	}

	public void unfollow(Long currentUserId, Integer targetGithubUserId) {

		Integer currentUserGithubId = userRepository.findById(currentUserId).orElseThrow(UserNotFoundException::new).getGithubUserId();
		String accessToken = githubOAuthTokenReader.getGithubAccessToken(currentUserId);

		followRelationRepository.unfollow(currentUserId, currentUserGithubId, targetGithubUserId, accessToken);
	}
}

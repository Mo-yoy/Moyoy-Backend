package com.moyoy.api.github_follow.application;

import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import com.moyoy.api.github_follow.application.request.GithubFollowDetectionData;
import com.moyoy.api.github_follow.application.response.GithubFollowDetectionResult;
import com.moyoy.domain.follow.FollowRelation;
import com.moyoy.domain.follow.FollowUser;
import com.moyoy.domain.support.error.user.UserNotFoundException;
import com.moyoy.domain.user.User;
import com.moyoy.domain.user.UserRepository;
import com.moyoy.infra.external.github.follow.GithubFollowClient;

@Service
@RequiredArgsConstructor
public class GithubFollowService {

	private final UserRepository userRepository;
	private final GithubFollowClient githubFollowClient;

	public GithubFollowDetectionResult detect(Long userId, GithubFollowDetectionData data) {

		User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
		Integer githubUserId = user.getGithubUserId();

		List<?> githubFollowings = githubFollowClient.fetchFollowings(userId, githubUserId);
		List<?> githubFollowers = githubFollowClient.fetchFollowers(userId, githubUserId);


	}

	public GithubFollowDetectionResult getFollowUserSlice(Long userId, GithubFollowDetectionData followDetection) {

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

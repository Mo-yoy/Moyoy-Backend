package com.moyoy.api.github_follow.application;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.moyoy.api.common.util.SlicePagingUtils;
import com.moyoy.api.github_follow.application.request.GithubFollowDetectionData;
import com.moyoy.api.github_follow.application.response.GithubFollowDetectionResult;
import com.moyoy.domain.follow.DetectType;
import com.moyoy.domain.follow.GithubFollowClassifier;
import com.moyoy.domain.follow.GithubUser;
import com.moyoy.domain.support.error.user.UserNotFoundException;
import com.moyoy.domain.support.page.SliceResult;
import com.moyoy.domain.user.User;
import com.moyoy.domain.user.UserRepository;
import com.moyoy.infra.cache.GithubFollowCacheManager;
import com.moyoy.infra.cache.GithubFollowRelationSnapshot;
import com.moyoy.infra.external.github.follow.GithubFollowClient;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GithubFollowService {

	private final UserRepository userRepository;
	private final GithubFollowClient githubFollowClient;
	private final GithubFollowClassifier githubFollowClassifier;
	private final GithubFollowCacheManager githubFollowCacheManager;

	public Optional<GithubFollowDetectionResult> detect(Long userId, GithubFollowDetectionData data) {

		Optional<GithubFollowRelationSnapshot> followRelationSnapshot = githubFollowCacheManager.findFollowSnapshot(userId);

		if(followRelationSnapshot.isEmpty()) {

			///  TODO : SQS에 인큐 , 이미 작업이 들어있을 때 제어해야함. 분산락?

			return Optional.empty();
		}

		GithubFollowRelationSnapshot followRelation = followRelationSnapshot.get();
		List<GithubUser> githubFollowers = followRelation.getGithubFollowers();
		List<GithubUser> githubFollowings = followRelation.getGithubFollowings();

		DetectType detectType = data.detectType();
		List<GithubUser> classifiedUserList = githubFollowClassifier.classifyByDetectType(detectType, githubFollowers, githubFollowings);

		SliceResult<GithubUser> sliceResult = SlicePagingUtils.window(
			classifiedUserList,
			data.lastGithubUserId(),
			data.size(),
			GithubUser::id
		);

		GithubFollowDetectionResult result = GithubFollowDetectionResult.of(
			sliceResult,
			followRelation.getSnapshotTime(),
			classifiedUserList.size());

		return Optional.of(result);
	}

	public void follow(Long currentUserId, Integer targetUserGithubId) {

		User user = userRepository.findById(currentUserId).orElseThrow(UserNotFoundException::new);
		Integer githubUserId = user.getGithubUserId();

		githubFollowClient.follow(currentUserId, githubUserId, targetUserGithubId);
	}

	public void unfollow(Long currentUserId, Integer targetGithubUserId) {

		User user = userRepository.findById(currentUserId).orElseThrow(UserNotFoundException::new);
		Integer githubUserId = user.getGithubUserId();

		githubFollowClient.unFollow(currentUserId, githubUserId, targetGithubUserId);
	}
}

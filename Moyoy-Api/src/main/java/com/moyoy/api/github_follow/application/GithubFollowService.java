package com.moyoy.api.github_follow.application;

import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import com.moyoy.api.common.util.SlicePagingUtils;
import com.moyoy.api.github_follow.application.request.GithubFollowDetectionData;
import com.moyoy.api.github_follow.application.response.GithubFollowDetectionResult;

import com.moyoy.domain.github_follow.GithubFollowClassifier;
import com.moyoy.domain.github_follow.GithubFollowSnapshot;
import com.moyoy.domain.github_follow.GithubUser;
import com.moyoy.domain.support.error.github_follow.GithubFollowSnapshotCoolDownNotExpiredException;
import com.moyoy.domain.support.error.user.UserNotFoundException;
import com.moyoy.domain.support.page.SliceResult;
import com.moyoy.domain.user.User;
import com.moyoy.domain.user.UserRepository;

import com.moyoy.infra.database.redis.follow.GithubFollowSnapshotCacheManager;
import com.moyoy.infra.external.github.follow.GithubFollowClient;
import com.moyoy.infra.external.github.follow.GithubFollowSnapshotTaskManager;
import com.moyoy.infra.external.github.user.GithubUserClient;
import com.moyoy.infra.external.github.user.GithubUserResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class GithubFollowService {

	private final UserRepository userRepository;
	private final GithubFollowClient githubFollowClient;
	private final GithubFollowClassifier githubFollowClassifier;
	private final GithubFollowSnapshotCacheManager followSnapshotCacheManager;
	private final GithubFollowSnapshotTaskManager githubFollowSnapshotTaskManager;
	private final GithubUserClient githubUserClient;

	public Optional<GithubFollowDetectionResult> detect(Long userId, GithubFollowDetectionData data) {

		Optional<GithubFollowSnapshot> githubFollowSnapshot = followSnapshotCacheManager.findFollowSnapshot(userId);

		if (githubFollowSnapshot.isEmpty()) {

			User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
			githubFollowSnapshotTaskManager.submit(userId, user.getGithubUserId());
			return Optional.empty();
		}

		List<GithubUser> classifiedUserList = githubFollowClassifier.classifyByDetectType(data.detectType(), githubFollowSnapshot.get());

		SliceResult<GithubUser> sliceResult = SlicePagingUtils.window(
			classifiedUserList,
			data.lastGithubUserId(),
			data.size(),
			GithubUser::id);

		return Optional.of(
			GithubFollowDetectionResult.of(
				sliceResult,
				githubFollowSnapshot.get().getSnapshotTime(),
				classifiedUserList.size()));
	}

	public void refresh(Long currentUserId) {

		Optional<GithubFollowSnapshot> followSnapshot = followSnapshotCacheManager.findFollowSnapshot(currentUserId);
		if (followSnapshot.isPresent()) {

			boolean canRefresh = followSnapshot.get().canRefresh();

			if (!canRefresh) {
				log.warn("팔로우 캐시 5분 내 강제 갱신 시도 발생 | userId={}", currentUserId);
				throw new GithubFollowSnapshotCoolDownNotExpiredException();
			}

			followSnapshotCacheManager.delete(currentUserId);
		}

		User user = userRepository.findById(currentUserId).orElseThrow(UserNotFoundException::new);
		log.info("Github Follow Snapshot Refresh 시도, userId={}", currentUserId);
		githubFollowSnapshotTaskManager.submit(currentUserId, user.getGithubUserId());
	}

	public void follow(Long currentUserId, Integer targetUserGithubId) {

		User currentMoyoyUser = userRepository.findById(currentUserId).orElseThrow(UserNotFoundException::new);
		githubFollowClient.follow(currentUserId, targetUserGithubId);

		followSnapshotCacheManager.findFollowSnapshot(currentUserId)
			.ifPresent(currentUserSnapshot -> {

				GithubUser targetGithubUser = fromApi(currentUserId, targetUserGithubId);
				currentUserSnapshot.addFollowing(targetGithubUser);
				followSnapshotCacheManager.save(currentMoyoyUser.getId(), currentUserSnapshot);
			});

		Optional<User> targetMoyoyUser = userRepository.findByGithubUserId(targetUserGithubId);
		if (targetMoyoyUser.isEmpty())
			return;

		followSnapshotCacheManager.findFollowSnapshot(targetMoyoyUser.get().getId())
			.ifPresent(
				targetUserSnapshot -> {

					GithubUser currentGithubUser = fromUser(currentMoyoyUser);
					targetUserSnapshot.addFollower(currentGithubUser);
					followSnapshotCacheManager.save(targetMoyoyUser.get().getId(), targetUserSnapshot);
				});
	}

	public void unfollow(Long currentUserId, Integer targetUserGithubId) {

		User currentMoyoyUser = userRepository.findById(currentUserId).orElseThrow(UserNotFoundException::new);
		githubFollowClient.unFollow(currentUserId, targetUserGithubId);

		followSnapshotCacheManager.findFollowSnapshot(currentUserId)
			.ifPresent(currentUserSnapshot -> {

				GithubUser targetGithubUser = fromApi(currentUserId, targetUserGithubId);
				currentUserSnapshot.removeFollowing(targetGithubUser);
				followSnapshotCacheManager.save(currentMoyoyUser.getId(), currentUserSnapshot);
			});

		Optional<User> targetMoyoyUser = userRepository.findByGithubUserId(targetUserGithubId);
		if (targetMoyoyUser.isEmpty())
			return;

		followSnapshotCacheManager.findFollowSnapshot(targetMoyoyUser.get().getId())
			.ifPresent(
				targetUserSnapshot -> {

					GithubUser currentGithubUser = fromUser(currentMoyoyUser);
					targetUserSnapshot.removeFollower(currentGithubUser);
					followSnapshotCacheManager.save(targetMoyoyUser.get().getId(), targetUserSnapshot);
				});
	}

	private GithubUser fromApi(Long currentUserId, Integer targetGithubUserId) {

		GithubUserResponse res = githubUserClient.fetchUser(currentUserId, targetGithubUserId);
		return GithubUser.of(res.id(), res.login(), res.avatarUrl());
	}

	private GithubUser fromUser(User user) {
		return GithubUser.of(user.getGithubUserId(), user.getUsername(), user.getProfileImgUrl());
	}

}

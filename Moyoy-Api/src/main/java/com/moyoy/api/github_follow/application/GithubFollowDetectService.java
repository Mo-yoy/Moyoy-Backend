package com.moyoy.api.github_follow.application;

import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import com.moyoy.api.github_follow.application.event.DetectEvent;
import com.moyoy.api.github_follow.application.request.GithubFollowDetectionData;
import com.moyoy.api.github_follow.application.response.GithubFollowDetectionResult;
import com.moyoy.api.support.SlicePagingUtils;

import com.moyoy.domain.github_follow.GithubFollowClassifier;
import com.moyoy.domain.github_follow.GithubFollowSnapshot;
import com.moyoy.domain.github_follow.GithubUser;
import com.moyoy.domain.github_follow.error.GithubFollowSnapshotCoolDownNotExpiredException;
import com.moyoy.domain.user.User;
import com.moyoy.domain.user.UserRepository;
import com.moyoy.domain.user.error.UserNotFoundException;

import com.moyoy.infra.database.redis.follow.GithubFollowSnapshotCacheManager;

import com.moyoy.common.page.SliceResult;

@Slf4j
@Service
@RequiredArgsConstructor
public class GithubFollowDetectService {

	private final UserRepository userRepository;
	private final ApplicationEventPublisher eventPublisher;
	private final GithubFollowSnapshotCacheManager followSnapshotCacheManager;
	private final GithubFollowClassifier githubFollowClassifier;

	public Optional<GithubFollowDetectionResult> detect(Long userId, GithubFollowDetectionData data) {

		Optional<GithubFollowSnapshot> githubFollowSnapshot = followSnapshotCacheManager.findFollowSnapshot(userId);

		if (githubFollowSnapshot.isEmpty()) {

			User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
			eventPublisher.publishEvent(new DetectEvent(userId, user.getGithubUserId()));
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
		eventPublisher.publishEvent(new DetectEvent(currentUserId, user.getGithubUserId()));
	}
}

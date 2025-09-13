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

import com.moyoy.domain.user.User;
import com.moyoy.domain.user.UserRepository;
import com.moyoy.domain.user.error.UserNotFoundException;

import com.moyoy.infra.redis.cache.github_follow.GithubFollowCacheStore;
import com.moyoy.infra.redis.cache.github_follow.GithubFollowSnapshot;
import com.moyoy.infra.redis.cache.github_follow.GithubUserProfile;
import com.moyoy.infra.redis.cache.support.error.GithubFollowSnapshotCoolDownNotExpiredException;

import com.moyoy.common.page.SliceResult;

@Slf4j
@Service
@RequiredArgsConstructor
public class GithubFollowDetectService {

	private final UserRepository userRepository;
	private final GithubFollowCacheStore followCacheStore;
	private final ApplicationEventPublisher eventPublisher;

	public Optional<GithubFollowDetectionResult> detect(Long userId, GithubFollowDetectionData data) {

		Optional<GithubFollowSnapshot> githubFollowSnapshot = followCacheStore.findFollowSnapshot(userId);

		if (githubFollowSnapshot.isEmpty()) {

			User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
			DetectEvent detectEvent = DetectEvent.from(user);
			eventPublisher.publishEvent(detectEvent);
			return Optional.empty();
		}

		List<GithubUserProfile> filteredUserList = githubFollowSnapshot.get().filterByDetectType(data.detectType());

		SliceResult<GithubUserProfile> sliceResult = SlicePagingUtils.window(
			filteredUserList,
			data.lastGithubUserId(),
			data.size(),
			GithubUserProfile::id);

		GithubFollowDetectionResult detectionResult = GithubFollowDetectionResult.of(
			sliceResult,
			githubFollowSnapshot.get().getSnapshotTime(),
			filteredUserList.size());

		return Optional.of(detectionResult);
	}

	public void refresh(Long userId) {

		Optional<GithubFollowSnapshot> followSnapshot = followCacheStore.findFollowSnapshot(userId);

		if (followSnapshot.isPresent()) {

			boolean canRefresh = followSnapshot.get().canRefresh();

			if (!canRefresh) {

				log.warn("팔로우 캐시 5분 내 강제 갱신 시도 발생 | userId={}", userId);
				throw new GithubFollowSnapshotCoolDownNotExpiredException();
			}

			followCacheStore.delete(userId);
		}

		User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
		log.info("Github Follow Snapshot Refresh 시도, userId={}", userId);
		DetectEvent detectEvent = DetectEvent.from(user);
		eventPublisher.publishEvent(detectEvent);
	}
}

package com.moyoy.api.github_follow.application;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import com.moyoy.api.github_follow.application.event.DetectEvent;
import com.moyoy.api.github_follow.application.request.GithubFollowDetectionData;
import com.moyoy.api.github_follow.application.response.GithubFollowDetectionResult;

import com.moyoy.domain.github_follow.DetectType;
import com.moyoy.domain.github_follow.GithubFollowClassifier;
import com.moyoy.domain.github_follow.GithubFollowSnapshot;
import com.moyoy.domain.github_follow.GithubUser;
import com.moyoy.domain.support.error.github_follow.GithubFollowSnapshotCoolDownNotExpiredException;
import com.moyoy.domain.user.User;
import com.moyoy.domain.user.UserRepository;

import com.moyoy.infra.database.redis.follow.GithubFollowSnapshotCacheManager;

@ExtendWith(MockitoExtension.class)
class GithubFollowDetectServiceTest {

	@InjectMocks
	private GithubFollowDetectService githubFollowDetectService;

	@Mock
	private UserRepository userRepository;

	@Mock
	private ApplicationEventPublisher eventPublisher;

	@Mock
	private GithubFollowSnapshotCacheManager followSnapshotCacheManager;

	@Mock
	private GithubFollowClassifier githubFollowClassifier;

	@Nested
	@DisplayName("Github 맞팔 탐지기 조회 요청시 ")
	class Detect {

		@Test
		void 캐시에_데이터_없으면_이벤트발행하고_empty리턴() {

			// given
			Long userId = 1L;

			User user = User.builder()
				.id(userId)
				.build();

			given(followSnapshotCacheManager.findFollowSnapshot(userId)).willReturn(Optional.empty());
			given(userRepository.findById(userId)).willReturn(Optional.of(user));

			// when
			Optional<GithubFollowDetectionResult> result = githubFollowDetectService.detect(userId, new GithubFollowDetectionData(DetectType.MUTUAL, 0, 10));

			// then
			assertThat(result).isEmpty();
			verify(eventPublisher).publishEvent(any(DetectEvent.class));
		}

		@Test
		void 캐시에_데이터_있으면_분류하고_결과리턴() {

			// given
			GithubUser githubUser = new GithubUser(123, "moyoy", "avatar");
			List<GithubUser> githubUserList = List.of(githubUser);

			LocalDateTime snapshotTime = LocalDateTime.of(2020, 1, 1, 0, 0);

			Long userId = 1L;
			GithubFollowSnapshot snapshot = GithubFollowSnapshot.of(githubUserList, githubUserList, snapshotTime);

			given(followSnapshotCacheManager.findFollowSnapshot(userId)).willReturn(Optional.of(snapshot));
			given(githubFollowClassifier.classifyByDetectType(any(), eq(snapshot)))
				.willReturn(List.of(new GithubUser(123, "moyoy", "avatar")));

			// when
			Optional<GithubFollowDetectionResult> result = githubFollowDetectService.detect(userId, new GithubFollowDetectionData(DetectType.MUTUAL, 0, 10));

			// then
			assertThat(result).isPresent();
			assertThat(result.get().totalFollowUserCount()).isEqualTo(1);
			verify(eventPublisher, never()).publishEvent(any());
		}
	}

	@Nested
	@DisplayName("Github 맞팔 탐지기 조회 캐시 데이터 강재갱신 요청시 ")
	class Refresh {

		@Test
		void 캐시있고_canRefresh_false면_예외발생() {

			// given
			Long userId = 1L;
			GithubFollowSnapshot snapshot = mock(GithubFollowSnapshot.class);
			given(snapshot.canRefresh()).willReturn(false);
			given(followSnapshotCacheManager.findFollowSnapshot(userId)).willReturn(Optional.of(snapshot));

			// when & then
			assertThatThrownBy(() -> githubFollowDetectService.refresh(userId))
				.isInstanceOf(GithubFollowSnapshotCoolDownNotExpiredException.class);

			verify(eventPublisher, never()).publishEvent(any());
		}

		@Test
		void 캐시가없거나_canRefresh_true면_캐시를_삭제하고_이벤트발행() {

			// given
			Long userId = 1L;
			User user = User.builder().id(userId).build();

			GithubFollowSnapshot snapshot = mock(GithubFollowSnapshot.class);
			given(snapshot.canRefresh()).willReturn(true);
			given(followSnapshotCacheManager.findFollowSnapshot(userId)).willReturn(Optional.of(snapshot));
			given(userRepository.findById(userId)).willReturn(Optional.of(user));

			// when
			githubFollowDetectService.refresh(userId);

			// then
			verify(followSnapshotCacheManager).delete(userId);
			verify(eventPublisher).publishEvent(any(DetectEvent.class));
		}
	}

}

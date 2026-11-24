package com.moyoy.infra.redis.pr_review;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.moyoy.domain.pr_review.PrReviewHit;
import com.moyoy.domain.pr_review.PrReviewHitCount;
import com.moyoy.domain.pr_review.dto.PrReviewHitCreate;

@Testcontainers
@DataRedisTest
@Import(PrReviewHitRepositoryImpl.class)
class PrReviewHitRepositoryImplTest {

	private static final LocalDateTime WINDOW_14 = LocalDateTime.of(2026, 11, 24, 14, 0);
	private static final LocalDateTime AFTER_WINDOW_14_CLOSED = LocalDateTime.of(2026, 11, 24, 15, 1);

	@Container
	static GenericContainer<?> redis = new GenericContainer<>("redis:7.4").withExposedPorts(6379);

	@DynamicPropertySource
	static void redisProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.data.redis.host", redis::getHost);
		registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379));
	}

	@Autowired
	private PrReviewHitRepositoryImpl prReviewHitRepository;

	@Autowired
	private StringRedisTemplate stringRedisTemplate;

	@BeforeEach
	void setUp() {
		stringRedisTemplate.getConnectionFactory().getConnection().serverCommands().flushDb();
	}

	@Test
	@DisplayName("같은 구간에서 같은 조회자는 한 번만 센다.")
	void sameViewerInWindow() {
		prReviewHitRepository.save(hit(1L, 10L, WINDOW_14.plusMinutes(5)));
		prReviewHitRepository.save(hit(1L, 10L, WINDOW_14.plusMinutes(50)));

		assertThat(prReviewHitRepository.popClosedWindowCounts(AFTER_WINDOW_14_CLOSED))
			.extracting(PrReviewHitCount::getPrReviewId, PrReviewHitCount::getCount)
			.containsExactly(tuple(1L, 1));
	}

	@Test
	@DisplayName("요청글별로 고유 조회자 수를 센다.")
	void countsByReview() {
		prReviewHitRepository.save(hit(1L, 10L, WINDOW_14));
		prReviewHitRepository.save(hit(1L, 11L, WINDOW_14));
		prReviewHitRepository.save(hit(2L, 10L, WINDOW_14));

		assertThat(prReviewHitRepository.popClosedWindowCounts(AFTER_WINDOW_14_CLOSED))
			.extracting(PrReviewHitCount::getPrReviewId, PrReviewHitCount::getCount)
			.containsExactlyInAnyOrder(tuple(1L, 2), tuple(2L, 1));
	}

	@Test
	@DisplayName("구간이 다르면 같은 조회자도 다시 세고, 여러 구간은 합쳐서 꺼낸다.")
	void sameViewerInDifferentWindows() {
		prReviewHitRepository.save(hit(1L, 10L, WINDOW_14.plusMinutes(59)));
		prReviewHitRepository.save(hit(1L, 10L, WINDOW_14.plusMinutes(61)));

		LocalDateTime afterWindow15Closed = WINDOW_14.plusHours(2).plusMinutes(1);
		assertThat(prReviewHitRepository.popClosedWindowCounts(afterWindow15Closed))
			.extracting(PrReviewHitCount::getPrReviewId, PrReviewHitCount::getCount)
			.containsExactly(tuple(1L, 2));
	}

	@Test
	@DisplayName("끝나지 않았거나 종료 후 대기 시간이 지나지 않은 구간은 꺼내지 않는다.")
	void notClosedWindow() {
		prReviewHitRepository.save(hit(1L, 10L, WINDOW_14));

		assertThat(prReviewHitRepository.popClosedWindowCounts(WINDOW_14.plusMinutes(30))).isEmpty();
		assertThat(prReviewHitRepository.popClosedWindowCounts(WINDOW_14.plusMinutes(60).plusSeconds(30))).isEmpty();
		assertThat(prReviewHitRepository.popClosedWindowCounts(AFTER_WINDOW_14_CLOSED)).hasSize(1);
	}

	@Test
	@DisplayName("꺼낸 구간은 비워져서 다시 꺼내지 않는다.")
	void popClosedWindowCounts_clear() {
		prReviewHitRepository.save(hit(1L, 10L, WINDOW_14));
		prReviewHitRepository.popClosedWindowCounts(AFTER_WINDOW_14_CLOSED);

		assertThat(prReviewHitRepository.popClosedWindowCounts(AFTER_WINDOW_14_CLOSED)).isEmpty();
		assertThat(stringRedisTemplate.keys("pr_review_hit*")).isEmpty();
	}

	@Test
	@DisplayName("기록한 키에는 안전장치용 TTL이 걸린다.")
	void save_ttl() {
		prReviewHitRepository.save(hit(1L, 10L, WINDOW_14));

		assertThat(stringRedisTemplate.getExpire("pr_review_hit::2026112414::1")).isPositive();
		assertThat(stringRedisTemplate.getExpire("pr_review_hit_window::2026112414")).isPositive();
	}

	@Test
	@DisplayName("같은 조회자가 동시에 100번 조회해도 한 번만 센다.")
	void save_concurrent() throws InterruptedException {
		int requestCount = 100;
		ExecutorService executor = Executors.newFixedThreadPool(32);
		CountDownLatch latch = new CountDownLatch(requestCount);

		for (int i = 0; i < requestCount; i++) {
			executor.submit(() -> {
				try {
					prReviewHitRepository.save(hit(1L, 10L, WINDOW_14));
				} finally {
					latch.countDown();
				}
			});
		}
		latch.await();
		executor.shutdown();

		assertThat(prReviewHitRepository.popClosedWindowCounts(AFTER_WINDOW_14_CLOSED))
			.extracting(PrReviewHitCount::getCount)
			.containsExactly(1);
	}

	@ParameterizedTest(name = "고유 조회자 {0}명")
	@ValueSource(ints = {10, 100, 1_000, 10_000})
	@DisplayName("HyperLogLog 추정 오차는 표준 오차(0.81%)의 3배 이내다.")
	void accuracy(int viewerCount) {
		for (long userId = 1; userId <= viewerCount; userId++) {
			prReviewHitRepository.save(hit(1L, userId, WINDOW_14));
		}

		List<PrReviewHitCount> hitCounts = prReviewHitRepository.popClosedWindowCounts(AFTER_WINDOW_14_CLOSED);
		int estimated = hitCounts.get(0).getCount();
		double errorRate = Math.abs(estimated - viewerCount) * 100.0 / viewerCount;
		System.out.printf("[HLL 정확도] 실제 %d명 → 추정 %d명 (오차 %.2f%%)%n", viewerCount, estimated, errorRate);

		assertThat(errorRate).isLessThanOrEqualTo(0.81 * 3);
	}

	private PrReviewHit hit(Long prReviewId, Long userId, LocalDateTime viewedAt) {
		return PrReviewHit.create(new PrReviewHitCreate(prReviewId, userId, "127.0.0.1", viewedAt));
	}
}

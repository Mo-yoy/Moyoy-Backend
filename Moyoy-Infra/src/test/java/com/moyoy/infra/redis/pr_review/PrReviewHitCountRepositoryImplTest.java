package com.moyoy.infra.redis.pr_review;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.moyoy.domain.pr_review.PrReviewHitCount;

@Testcontainers
@DataRedisTest
@Import(PrReviewHitCountRepositoryImpl.class)
class PrReviewHitCountRepositoryImplTest {

	@Container
	static GenericContainer<?> redis = new GenericContainer<>("redis:7.4").withExposedPorts(6379);

	@DynamicPropertySource
	static void redisProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.data.redis.host", redis::getHost);
		registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379));
	}

	@Autowired
	private PrReviewHitCountRepositoryImpl prReviewHitCountRepository;

	@Autowired
	private StringRedisTemplate stringRedisTemplate;

	@BeforeEach
	void setUp() {
		stringRedisTemplate.getConnectionFactory().getConnection().serverCommands().flushDb();
	}

	private PrReviewHitCount hitCount(Long prReviewId, int count) {
		return PrReviewHitCount.builder().prReviewId(prReviewId).count(count).build();
	}

	@Test
	@DisplayName("요청글별로 보관된 증가분을 모두 꺼낸다.")
	void popAll() {
		prReviewHitCountRepository.restore(hitCount(1L, 1));
		prReviewHitCountRepository.restore(hitCount(1L, 1));
		prReviewHitCountRepository.restore(hitCount(2L, 1));

		List<PrReviewHitCount> hitCounts = prReviewHitCountRepository.popAll();

		assertThat(hitCounts)
			.extracting(PrReviewHitCount::getPrReviewId, PrReviewHitCount::getCount)
			.containsExactlyInAnyOrder(tuple(1L, 2), tuple(2L, 1));
	}

	@Test
	@DisplayName("꺼낸 증가분은 비워져서 다시 꺼내지 않는다.")
	void popAll_clear() {
		prReviewHitCountRepository.restore(hitCount(1L, 1));
		prReviewHitCountRepository.popAll();

		assertThat(prReviewHitCountRepository.popAll()).isEmpty();
	}

	@Test
	@DisplayName("꺼낸 뒤에 보관된 증가분은 다음 반영 때 꺼낸다.")
	void popAll_afterPop() {
		prReviewHitCountRepository.restore(hitCount(1L, 1));
		prReviewHitCountRepository.popAll();

		prReviewHitCountRepository.restore(hitCount(1L, 1));

		assertThat(prReviewHitCountRepository.popAll())
			.extracting(PrReviewHitCount::getPrReviewId, PrReviewHitCount::getCount)
			.containsExactly(tuple(1L, 1));
	}

	@Test
	@DisplayName("같은 요청글의 증가분은 합쳐서 꺼낸다.")
	void restore_merge() {
		prReviewHitCountRepository.restore(hitCount(1L, 1));
		prReviewHitCountRepository.restore(hitCount(1L, 1));

		assertThat(prReviewHitCountRepository.popAll())
			.extracting(PrReviewHitCount::getPrReviewId, PrReviewHitCount::getCount)
			.containsExactly(tuple(1L, 2));
	}

	@Test
	@DisplayName("동시에 100번 보관해도 유실 없이 누적된다.")
	void restore_concurrent() throws InterruptedException {
		int requestCount = 100;
		ExecutorService executor = Executors.newFixedThreadPool(32);
		CountDownLatch latch = new CountDownLatch(requestCount);

		for (int i = 0; i < requestCount; i++) {
			executor.submit(() -> {
				try {
					prReviewHitCountRepository.restore(hitCount(1L, 1));
				} finally {
					latch.countDown();
				}
			});
		}
		latch.await();
		executor.shutdown();

		assertThat(prReviewHitCountRepository.popAll())
			.extracting(PrReviewHitCount::getCount)
			.containsExactly(requestCount);
	}
}

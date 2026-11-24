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

	@Test
	@DisplayName("요청글별로 누적된 증가분을 모두 꺼낸다.")
	void popAll() {
		prReviewHitCountRepository.increase(1L);
		prReviewHitCountRepository.increase(1L);
		prReviewHitCountRepository.increase(2L);

		List<PrReviewHitCount> hitCounts = prReviewHitCountRepository.popAll();

		assertThat(hitCounts)
			.extracting(PrReviewHitCount::getPrReviewId, PrReviewHitCount::getCount)
			.containsExactlyInAnyOrder(tuple(1L, 2), tuple(2L, 1));
	}

	@Test
	@DisplayName("꺼낸 증가분은 비워져서 다시 꺼내지 않는다.")
	void popAll_clear() {
		prReviewHitCountRepository.increase(1L);
		prReviewHitCountRepository.popAll();

		assertThat(prReviewHitCountRepository.popAll()).isEmpty();
	}

	@Test
	@DisplayName("꺼낸 뒤에 들어온 증가분은 다음 반영 때 꺼낸다.")
	void popAll_afterPop() {
		prReviewHitCountRepository.increase(1L);
		prReviewHitCountRepository.popAll();

		prReviewHitCountRepository.increase(1L);

		assertThat(prReviewHitCountRepository.popAll())
			.extracting(PrReviewHitCount::getPrReviewId, PrReviewHitCount::getCount)
			.containsExactly(tuple(1L, 1));
	}

	@Test
	@DisplayName("되돌린 증가분은 새로 들어온 증가분과 합쳐서 다시 꺼낸다.")
	void restore() {
		prReviewHitCountRepository.increase(1L);
		PrReviewHitCount failed = prReviewHitCountRepository.popAll().get(0);

		prReviewHitCountRepository.increase(1L);
		prReviewHitCountRepository.restore(failed);

		assertThat(prReviewHitCountRepository.popAll())
			.extracting(PrReviewHitCount::getPrReviewId, PrReviewHitCount::getCount)
			.containsExactly(tuple(1L, 2));
	}

	@Test
	@DisplayName("동시에 100번 증가해도 유실 없이 누적된다.")
	void increase_concurrent() throws InterruptedException {
		int requestCount = 100;
		ExecutorService executor = Executors.newFixedThreadPool(32);
		CountDownLatch latch = new CountDownLatch(requestCount);

		for (int i = 0; i < requestCount; i++) {
			executor.submit(() -> {
				try {
					prReviewHitCountRepository.increase(1L);
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

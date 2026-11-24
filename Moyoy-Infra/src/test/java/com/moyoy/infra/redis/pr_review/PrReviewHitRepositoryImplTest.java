package com.moyoy.infra.redis.pr_review;

import static org.assertj.core.api.Assertions.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

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

import com.moyoy.domain.pr_review.PrReviewHit;
import com.moyoy.domain.pr_review.dto.PrReviewHitCreate;

@Testcontainers
@DataRedisTest
@Import(PrReviewHitRepositoryImpl.class)
class PrReviewHitRepositoryImplTest {

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
	@DisplayName("중복 판단 기간 내 첫 조회만 기록된다.")
	void saveIfAbsent_onlyFirstView() {
		PrReviewHit hit = PrReviewHit.create(new PrReviewHitCreate(1L, 10L, "127.0.0.1"));

		assertThat(prReviewHitRepository.saveIfAbsent(hit)).isTrue();
		assertThat(prReviewHitRepository.saveIfAbsent(hit)).isFalse();
	}

	@Test
	@DisplayName("조회자나 요청글이 다르면 각각 기록된다.")
	void saveIfAbsent_differentViewerOrReview() {
		PrReviewHit user10Review1 = PrReviewHit.create(new PrReviewHitCreate(1L, 10L, "127.0.0.1"));
		PrReviewHit user11Review1 = PrReviewHit.create(new PrReviewHitCreate(1L, 11L, "127.0.0.1"));
		PrReviewHit user10Review2 = PrReviewHit.create(new PrReviewHitCreate(2L, 10L, "127.0.0.1"));

		assertThat(prReviewHitRepository.saveIfAbsent(user10Review1)).isTrue();
		assertThat(prReviewHitRepository.saveIfAbsent(user11Review1)).isTrue();
		assertThat(prReviewHitRepository.saveIfAbsent(user10Review2)).isTrue();
	}

	@Test
	@DisplayName("기록에는 중복 판단 기간만큼 TTL이 걸린다.")
	void saveIfAbsent_ttl() {
		PrReviewHit hit = PrReviewHit.create(new PrReviewHitCreate(1L, 10L, "127.0.0.1"));

		prReviewHitRepository.saveIfAbsent(hit);

		Long ttl = stringRedisTemplate.getExpire("pr_review_hit::1::user::10");
		assertThat(ttl).isBetween(PrReviewHit.DUPLICATE_WINDOW.getSeconds() - 5, PrReviewHit.DUPLICATE_WINDOW.getSeconds());
	}

	@Test
	@DisplayName("TTL이 만료되면 다시 기록된다.")
	void saveIfAbsent_afterExpire() {
		PrReviewHit hit = PrReviewHit.create(new PrReviewHitCreate(1L, 10L, "127.0.0.1"));
		prReviewHitRepository.saveIfAbsent(hit);

		stringRedisTemplate.delete("pr_review_hit::1::user::10");

		assertThat(prReviewHitRepository.saveIfAbsent(hit)).isTrue();
	}

	@Test
	@DisplayName("같은 조회자가 동시에 100번 조회해도 한 번만 기록된다.")
	void saveIfAbsent_concurrent() throws InterruptedException {
		int requestCount = 100;
		ExecutorService executor = Executors.newFixedThreadPool(32);
		CountDownLatch latch = new CountDownLatch(requestCount);
		AtomicInteger savedCount = new AtomicInteger();
		PrReviewHit hit = PrReviewHit.create(new PrReviewHitCreate(1L, 10L, "127.0.0.1"));

		for (int i = 0; i < requestCount; i++) {
			executor.submit(() -> {
				try {
					if (prReviewHitRepository.saveIfAbsent(hit))
						savedCount.incrementAndGet();
				} finally {
					latch.countDown();
				}
			});
		}
		latch.await();
		executor.shutdown();

		assertThat(savedCount.get()).isEqualTo(1);
	}
}

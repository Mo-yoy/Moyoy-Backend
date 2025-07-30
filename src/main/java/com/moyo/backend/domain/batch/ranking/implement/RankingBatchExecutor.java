package com.moyo.backend.domain.batch.ranking.implement;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import jakarta.annotation.PreDestroy;

@Slf4j
@Component
public class RankingBatchExecutor {

	private static final int RANKING_BATCH_THREAD_POOL_SIZE = 2;

	private final ExecutorService executorService;

	public RankingBatchExecutor() {
		this.executorService = new ThreadPoolExecutor(
			RANKING_BATCH_THREAD_POOL_SIZE,
			RANKING_BATCH_THREAD_POOL_SIZE,
			0L, TimeUnit.SECONDS,
			new ArrayBlockingQueue<>(RANKING_BATCH_THREAD_POOL_SIZE),
			new ThreadFactory() {

				private final AtomicInteger threadNum = new AtomicInteger(1);

				@Override
				public Thread newThread(Runnable r) {
					return new Thread(r, "ranking-batch-executor-service-thread-" + threadNum.getAndIncrement());
				}
			});
		log.info("ExecutorService has been created with a pool size of " + RANKING_BATCH_THREAD_POOL_SIZE);
	}

	public List<Future<RankingBatchTaskResult>> invokeAll(List<Callable<RankingBatchTaskResult>> tasks) {
		try {
			return executorService.invokeAll(tasks);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	@PreDestroy
	protected void close() {
		///  TODO close() 구현 , 무조건 1일 대기임 현재는
		log.info("Shutting down ExecutorService");
		executorService.close();
	}
}

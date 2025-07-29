package com.moyo.backend.domain.batch.ranking.service;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Component;

import com.moyo.backend.domain.github_ranking.implement.Ranking;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class RankingBatchExecutorService {

	private static final int RANKING_BATCH_SIZE = 2;
	private final ExecutorService executorService;

	public RankingBatchExecutorService() {
		this.executorService = new ThreadPoolExecutor(
			RANKING_BATCH_SIZE,
			RANKING_BATCH_SIZE,
			0L, TimeUnit.SECONDS,
			new ArrayBlockingQueue<>(RANKING_BATCH_SIZE),
			new ThreadFactory() {

				private final AtomicInteger threadNum = new AtomicInteger(1);

				@Override
				public Thread newThread(Runnable r) {
					return new Thread(r, "ranking-batch-executor-service-thread-" + threadNum.getAndIncrement());
				}
			}
		);
		log.info("ExecutorService has been created with a pool size of " + RANKING_BATCH_SIZE);
	}

	public Future<Ranking> submit(Callable<Ranking> task) {
		try {
			return executorService.submit(task);
		} catch (Exception e) {
			log.error("Task submission failed", e);
			throw e;
		}
	}

	public List<Future<Ranking>> submitAll(List<Callable<Ranking>> tasks) {
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

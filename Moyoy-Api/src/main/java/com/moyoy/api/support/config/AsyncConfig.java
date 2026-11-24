package com.moyoy.api.support.config;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class AsyncConfig {

	@Bean(name = "followLoadExecutor")
	public Executor taskExecutor() {

		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

		executor.setCorePoolSize(3);
		executor.setMaxPoolSize(3);
		executor.setQueueCapacity(300);
		executor.setThreadNamePrefix("FollowLoad-");

		executor.initialize();
		return executor;
	}

	@Bean(name = "hitsExecutor")
	public Executor hitsExecutor() {

		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

		executor.setCorePoolSize(3);
		executor.setMaxPoolSize(3);
		executor.setQueueCapacity(300);
		executor.setThreadNamePrefix("PrReviewHits-");
		/// 큐가 가득 차면 조회수 증가 작업만 버리고, 요청글 조회 응답은 정상 처리
		executor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy());

		executor.initialize();
		return executor;
	}
}

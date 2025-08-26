package com.moyoy.api.common.config;

import java.util.concurrent.Executor;

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
}

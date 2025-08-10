package com.moyoy.batch.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
@EnableScheduling
public class SchedulerConfig {

	/// 현재는 랭킹 업데이트 밖에 없음. 추후 다른 배치작업들이 추가 될 예정
	@Bean
	public TaskScheduler batchScheduler() {
		ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
		scheduler.setPoolSize(1);
		scheduler.setThreadNamePrefix("Moyoy-Batch-Scheduler-");
		scheduler.initialize();
		return scheduler;
	}
}

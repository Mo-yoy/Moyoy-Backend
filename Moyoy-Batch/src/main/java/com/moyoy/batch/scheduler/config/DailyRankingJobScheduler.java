package com.moyoy.batch.scheduler.config;

import java.util.Date;

import lombok.RequiredArgsConstructor;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@RequiredArgsConstructor
public class DailyRankingJobScheduler {

	private final JobLauncher jobLauncher;
	private final Job rankCalculationJob;

	@Scheduled(cron = "00 00 00 * * *")
	public void runRankingJob() throws Exception {

		JobParameters params = new JobParametersBuilder()
			.addDate("batchStartTime", new Date())
			.toJobParameters();

		jobLauncher.run(rankCalculationJob, params);
	}
}

package com.moyoy.batch.job;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;

import javax.sql.DataSource;

import lombok.RequiredArgsConstructor;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.batch.item.support.builder.CompositeItemProcessorBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import com.moyoy.batch.job.processor.CalculateRankingResultProcessor;
import com.moyoy.batch.job.processor.CreateUserAuthContextProcessor;
import com.moyoy.batch.job.processor.FetchContributorStatsProcessor;
import com.moyoy.batch.job.processor.FetchGithubProfileProcessor;
import com.moyoy.batch.job.processor.PrepareRankingRepoCandidatesProcessor;
import com.moyoy.batch.job.processor.dto.UserRankResult;
import com.moyoy.batch.listener.RankingJobDiscordListener;

import com.moyoy.domain.ranking.dto.RankingCalculatorResult;

import com.moyoy.infra.database.mysql.user.UserEntity;
import com.moyoy.infra.external.github.support.error.GithubPollingApiTimeOutException;
import com.moyoy.infra.external.github.support.error.GithubPreCheckLimitExceedException;

import jakarta.persistence.EntityManagerFactory;

@Configuration
@RequiredArgsConstructor
public class RankCalculationJobConfig {

	private final RankingJobDiscordListener rankingJobDiscordListener;

	@Bean
	public Job rankCalculationJob(JobRepository jobRepository, Step userRankingCalculationStep) {

		return new JobBuilder("rankCalculationJob", jobRepository)
			.listener(rankingJobDiscordListener)
			.start(userRankingCalculationStep)
			.build();
	}

	@Bean
	public Step userRankingCalculationStep(
		JobRepository jobRepository,
		PlatformTransactionManager transactionManager,
		DataSource dataSource,
		ItemReader<UserEntity> userEntityReader,
		ItemProcessor<UserEntity, UserRankResult> userRankingCalculateProcessor) {

		return new StepBuilder("userRankingCalculationStep", jobRepository)
			.<UserEntity, UserRankResult>chunk(10, transactionManager)
			.reader(userEntityReader)
			.processor(userRankingCalculateProcessor)
			.writer(userRankingWriter(dataSource))
			.faultTolerant()
			.skip(GithubPollingApiTimeOutException.class)
			.skip(GithubPreCheckLimitExceedException.class)
			.skipLimit(Integer.MAX_VALUE)
			.build();
	}

	@Bean
	@StepScope
	public JpaPagingItemReader<UserEntity> userEntityReader(
		@Value("#{jobParameters['batchStartTime']}") Date batchStartTime,
		EntityManagerFactory entityManagerFactory) {

		LocalDateTime cutoff = LocalDateTime.ofInstant(
			batchStartTime.toInstant(),
			ZoneId.systemDefault());

		return new JpaPagingItemReaderBuilder<UserEntity>()
			.name("userEntityReader")
			.entityManagerFactory(entityManagerFactory)
			.queryString("SELECT u FROM UserEntity u WHERE u.createdAt <= :cutoff ORDER BY u.id")
			.parameterValues(Map.of("cutoff", cutoff))
			.pageSize(10)
			.build();
	}

	@Bean
	public ItemProcessor<UserEntity, UserRankResult> userRankingCalculateProcessor(
		CreateUserAuthContextProcessor createUserAuthContextProcessor,
		FetchGithubProfileProcessor fetchGithubProfileProcessor,
		PrepareRankingRepoCandidatesProcessor prepareRankingRepoCandidatesProcessor,
		FetchContributorStatsProcessor fetchContributorStatsProcessor,
		CalculateRankingResultProcessor calculateRankingResultProcessor) {
		return new CompositeItemProcessorBuilder<UserEntity, UserRankResult>()
			.delegates(
				createUserAuthContextProcessor,
				fetchGithubProfileProcessor,
				prepareRankingRepoCandidatesProcessor,
				fetchContributorStatsProcessor,
				calculateRankingResultProcessor)
			.build();
	}

	@Bean
	public ItemWriter<UserRankResult> userRankingWriter(DataSource dataSource) {

		String sql = """
			UPDATE rankings
			SET weekly_point = ?,
			    monthly_point = ?,
			    yearly_point  = ?,
			    grade         = ?,
			    modified_at   = NOW(6)
			WHERE user_id = ?
			""";

		return new JdbcBatchItemWriterBuilder<UserRankResult>()
			.dataSource(dataSource)
			.sql(sql)
			.itemPreparedStatementSetter((userRankResult, preparedStatement) -> {
				RankingCalculatorResult rankingCalculatorResult = userRankResult.rankingCalculatorResult();
				preparedStatement.setLong(1, rankingCalculatorResult.weekRankingPoint());
				preparedStatement.setLong(2, rankingCalculatorResult.monthRankingPoint());
				preparedStatement.setLong(3, rankingCalculatorResult.yearRankingPoint());
				preparedStatement.setString(4, rankingCalculatorResult.rankingGrade());
				preparedStatement.setLong(5, userRankResult.userId());
			})
			.assertUpdates(true)
			.build();
	}
}

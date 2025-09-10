package com.moyoy.batch.job;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

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

import com.moyoy.batch.dto.GithubContributorDetails;
import com.moyoy.batch.dto.GithubRepoCommitStats;
import com.moyoy.batch.dto.GithubRepoDetails;
import com.moyoy.batch.dto.RepoCandidatesContext;
import com.moyoy.batch.dto.UserAuthContext;
import com.moyoy.batch.dto.UserProfileContext;
import com.moyoy.batch.dto.UserRankResult;
import com.moyoy.batch.dto.UserSummaryContext;
import com.moyoy.batch.helper.GithubCommitStatCalculator;
import com.moyoy.batch.listener.RankingJobDiscordListener;

import com.moyoy.domain.ranking.GithubCommitStats;
import com.moyoy.domain.ranking.RankingCalculator;
import com.moyoy.domain.ranking.dto.RankingCalculatorParameters;
import com.moyoy.domain.ranking.dto.RankingCalculatorResult;

import com.moyoy.infra.database.mysql.user.UserEntity;
import com.moyoy.infra.external.github.support.GithubApiLimitChecker;
import com.moyoy.infra.database.mysql.support.OAuthTokenReader;
import com.moyoy.infra.external.github.repo.GithubRepoClient;
import com.moyoy.infra.external.github.user.GithubUserClient;
import com.moyoy.infra.external.github.user.dto.GithubUserResponse;

import jakarta.persistence.EntityManagerFactory;

@Configuration
@RequiredArgsConstructor
public class RankCalculationJobConfig {

	private final OAuthTokenReader OAuthTokenReader;
	private final GithubApiLimitChecker githubApiLimitChecker;
	private final GithubCommitStatCalculator githubCommitStatCalculator;
	private final GithubUserClient githubUserClient;
	private final GithubRepoClient githubRepoClient;
	private final RankingCalculator rankingCalculator;
	private final RankingJobDiscordListener rankingJobDiscordListener;

	@Bean
	public Job rankCalculationJob(JobRepository jobRepository, Step userRankingStep) {

		return new JobBuilder("rankCalculationJob", jobRepository)
			.listener(rankingJobDiscordListener)
			.start(userRankingStep)
			.build();
	}

	@Bean
	public Step userRankingStep(
		JobRepository jobRepository,
		PlatformTransactionManager transactionManager,
		DataSource dataSource,
		ItemReader<UserEntity> userReader) {

		return new StepBuilder("userRankingStep", jobRepository)
			.<UserEntity, UserRankResult>chunk(10, transactionManager)
			.reader(userReader)
			.processor(userRankingProcessor())
			.writer(userRankingWriter(dataSource))
			.build();
	}

	@Bean
	@StepScope
	public JpaPagingItemReader<UserEntity> userReader(
		@Value("#{jobParameters['batchStartTime']}") Date batchStartTime,
		EntityManagerFactory entityManagerFactory) {
		LocalDateTime cutoff = LocalDateTime.ofInstant(
			batchStartTime.toInstant(),
			ZoneId.systemDefault());

		return new JpaPagingItemReaderBuilder<UserEntity>()
			.name("userReader")
			.entityManagerFactory(entityManagerFactory)
			.queryString("SELECT u FROM UserEntity u WHERE u.createdAt <= :cutoff ORDER BY u.id")
			.parameterValues(Map.of("cutoff", cutoff))
			.pageSize(10)
			.build();
	}

	@Bean
	public ItemProcessor<UserEntity, UserRankResult> userRankingProcessor() {

		return new CompositeItemProcessorBuilder<UserEntity, UserRankResult>()
			.delegates(
				createUserAuthContext(),
				fetchUserProfile(),
				prepareRankingRepoCandidates(),
				fetchContributorStats(),
				calculateRankingResult())
			.build();
	}

	@Bean
	public ItemProcessor<UserEntity, UserAuthContext> createUserAuthContext() {

		return userEntity -> {

			String githubAccessToken = OAuthTokenReader.getGithubAccessToken(userEntity.getId());
			return new UserAuthContext(userEntity.getId(), userEntity.getGithubUserId(), githubAccessToken);
		};
	}

	@Bean
	public ItemProcessor<UserAuthContext, UserProfileContext> fetchUserProfile() {
		return auth -> {

			GithubUserResponse githubUserResponse = githubUserClient.fetchUser(auth.githubAccessToken(), auth.githubUserId());

			return new UserProfileContext(auth, githubUserResponse.login(), githubUserResponse.followers());
		};
	}

	@Bean
	public ItemProcessor<UserProfileContext, RepoCandidatesContext> prepareRankingRepoCandidates() {

		return userContext -> {

			String githubAccessToken = userContext.auth().githubAccessToken();
			String username = userContext.username();

			githubApiLimitChecker.assertCanGithubRequest(githubAccessToken, userContext.auth().githubUserId());

			List<GithubRepoDetails> githubRepoDetailsList = githubRepoClient.fetchReposCreatedThisYear(githubAccessToken)
				.stream()
				.map(GithubRepoDetails::from)
				.toList();

			List<GithubRepoDetails> userOwnedRepos = githubRepoDetailsList.stream()
				.filter(repo -> repo.ownerName().equals(username))
				.toList();

			List<GithubRepoDetails> userContributedRepos = githubRepoDetailsList.stream()
				.filter(repo -> !repo.ownerName().equals(username))
				.filter(repo -> {

					List<GithubContributorDetails> contributors = githubRepoClient.fetchRepoContributors(githubAccessToken, repo.repoFullName())
						.stream()
						.map(GithubContributorDetails::from)
						.toList();

					return contributors.stream().anyMatch(c -> c.username().equals(username));
				})
				.toList();

			List<GithubRepoDetails> candidateRepos = Stream.concat(
				userOwnedRepos.stream(),
				userContributedRepos.stream()).toList();

			return new RepoCandidatesContext(userContext, candidateRepos);
		};
	}

	@Bean
	public ItemProcessor<RepoCandidatesContext, UserSummaryContext> fetchContributorStats() {

		return repoCandidatesContext -> {

			String accessToken = repoCandidatesContext.userProfileContext().auth().githubAccessToken();
			String username = repoCandidatesContext.userProfileContext().username();

			githubApiLimitChecker.assertCanGithubRequest(accessToken, repoCandidatesContext.userProfileContext().auth().githubUserId());

			List<GithubRepoCommitStats> userCommitStats = new ArrayList<>();

			for (GithubRepoDetails repoDetails : repoCandidatesContext.candidateRepos()) {

				List<GithubRepoCommitStats> githubRepoCommitStatsList = githubRepoClient.fetchRepoContributorStats(repoDetails.repoFullName(), accessToken)
					.stream()
					.map(GithubRepoCommitStats::from)
					.toList();

				githubRepoCommitStatsList.stream()
					.filter(contributor -> contributor.author().username().equals(username))
					.findFirst()
					.ifPresent(userCommitStats::add);
			}

			GithubCommitStats githubCommitStats = githubCommitStatCalculator.calculateCommitStats(userCommitStats);

			return new UserSummaryContext(repoCandidatesContext, githubCommitStats);
		};
	}

	@Bean
	public ItemProcessor<UserSummaryContext, UserRankResult> calculateRankingResult() {

		return userSummaryContext -> {

			List<GithubRepoDetails> candidateRepos = userSummaryContext.repoCandidatesContext().candidateRepos();
			UserProfileContext userProfileContext = userSummaryContext.repoCandidatesContext().userProfileContext();
			Long userId = userProfileContext.auth().userId();

			int stars = candidateRepos.stream()
				.mapToInt(GithubRepoDetails::starCount)
				.sum();

			int followers = userProfileContext.followerCount();

			GithubCommitStats githubCommitStats = userSummaryContext.githubCommitStats();

			RankingCalculatorParameters rankingCalculatorParameters = RankingCalculatorParameters.of(stars, followers, githubCommitStats);
			RankingCalculatorResult rankingCalculatorResult = rankingCalculator.calculate(rankingCalculatorParameters);

			return new UserRankResult(userId, rankingCalculatorResult);
		};
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

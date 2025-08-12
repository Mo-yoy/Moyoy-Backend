package com.moyoy.batch.jobRepository.ranking;

import static com.moyoy.batch.jobRepository.ranking.RankingBatchStatus.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 *   추후 Spring Batch로 넘어갈 예정이라 임시로 DB Access에 JPA를 사용중임.
 *
 *   JPA 연관관계 매핑은 사용 X
 */

@Table(name = "ranking_batch_history")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RankingBatchHistory {

	@Id
	@Column(name = "ranking_batch_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String batchName;
	private String executedBy;
	private LocalDateTime startedAt;
	private LocalDateTime finishedAt;

	@Enumerated(EnumType.STRING)
	private RankingBatchStatus status;
	private int totalCount;
	private int successCount;
	private int failCount;

	@Builder
	public RankingBatchHistory(String batchName, String executedBy, LocalDateTime startedAt, LocalDateTime finishedAt, RankingBatchStatus status, int totalCount, int successCount, int failCount) {
		this.batchName = batchName;
		this.executedBy = executedBy;
		this.startedAt = startedAt;
		this.finishedAt = finishedAt;
		this.status = status;
		this.totalCount = totalCount;
		this.successCount = successCount;
	}

	public static RankingBatchHistory init(LocalDateTime startedAt, String currentThreadName) {

		String batchName = "Daily Ranking Batch : ";
		batchName += DateTimeFormatter.ofPattern("yyyy-MM-dd").format(startedAt);

		return RankingBatchHistory.builder()
			.batchName(batchName)
			.executedBy(currentThreadName)
			.startedAt(startedAt)
			.status(IN_PROGRESS)
			.totalCount(0)
			.successCount(0)
			.failCount(0)
			.build();
	}

	public void finalizeBatch(int successCount, int failCount, int totalCount) {

		this.successCount = successCount;
		this.failCount = failCount;
		this.totalCount = totalCount;
		this.finishedAt = LocalDateTime.now();

		if (successCount == totalCount) {
			this.status = SUCCESS;
		} else if (successCount == 0) {
			this.status = FAIL;
		} else {
			this.status = PARTIAL_SUCCESS;
		}
	}

}

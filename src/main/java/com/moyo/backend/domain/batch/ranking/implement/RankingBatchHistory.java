package com.moyo.backend.domain.batch.ranking.implement;

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
	private String detailMessage;

	@Builder
	public RankingBatchHistory(String batchName, String executedBy, LocalDateTime startedAt, LocalDateTime finishedAt, RankingBatchStatus status, int totalCount, int successCount, int failCount, String detailMessage) {
		this.batchName = batchName;
		this.executedBy = executedBy;
		this.startedAt = startedAt;
		this.finishedAt = finishedAt;
		this.status = status;
		this.totalCount = totalCount;
		this.successCount = successCount;
	}

	public static RankingBatchHistory init(LocalDateTime startedAt, String currentThreadName, int totalCount){

		String batchName = "Daily Ranking Batch : ";
		batchName += DateTimeFormatter.ofPattern("yyyy--MM--dd").format(startedAt);

		return RankingBatchHistory.builder()
			.batchName(batchName)
			.executedBy(currentThreadName)
			.startedAt(startedAt)
			.status(RankingBatchStatus.IN_PROGRESS)
			.totalCount(totalCount)
			.successCount(0)
			.failCount(0)
			.detailMessage("")
			.build();
	}
}

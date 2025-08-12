package com.moyoy.batch.jobRepository.ranking;

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

@Table(name = "ranking_batch_detail")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RankingBatchDetail {

	@Id
	@Column(name = "ranking_batch_detail")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Long rankingId;
	private Long rankingBatchId;

	@Enumerated(EnumType.STRING)
	private RankingBatchDetailStatus status;
	private String detailMessage;

	@Builder
	public RankingBatchDetail(Long rankingId, Long rankingBatchId, RankingBatchDetailStatus status, String detailMessage) {
		this.rankingId = rankingId;
		this.rankingBatchId = rankingBatchId;
		this.status = status;
		this.detailMessage = detailMessage;
	}

	public static RankingBatchDetail success(Long rankingBatchId, Long rankingId) {

		return RankingBatchDetail.builder()
			.rankingBatchId(rankingBatchId)
			.rankingId(rankingId)
			.status(RankingBatchDetailStatus.SUCCESS)
			.detailMessage("SUCCESS")
			.build();
	}

	public static RankingBatchDetail fail(Long rankingBatchId, Long rankingId, String errorMessage) {

		return RankingBatchDetail.builder()
			.rankingBatchId(rankingBatchId)
			.rankingId(rankingId)
			.status(RankingBatchDetailStatus.FAIL)
			.detailMessage(errorMessage)
			.build();
	}
}

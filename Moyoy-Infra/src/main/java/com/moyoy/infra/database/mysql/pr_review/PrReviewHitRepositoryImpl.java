package com.moyoy.infra.database.mysql.pr_review;

import java.time.LocalDateTime;

import lombok.RequiredArgsConstructor;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.moyoy.domain.pr_review.PrReviewHit;
import com.moyoy.domain.pr_review.PrReviewHitRepository;

@Repository
@RequiredArgsConstructor
public class PrReviewHitRepositoryImpl implements PrReviewHitRepository {

	private final JdbcTemplate jdbcTemplate;
	private final PrReviewHitJpaRepository prReviewHitJpaRepository;

	@Override
	public PrReviewHit findOrCreate(PrReviewHit hit) {

		String sql = """
			INSERT IGNORE INTO pr_review_hits (pr_review_id, user_id, last_increased_at)
			VALUES (?, ?, ?)
			""";

		jdbcTemplate.update(sql,
			hit.getPrReviewId(),
			hit.getUserId(),
			hit.getLastIncreasedAt());

		PrReviewHitEntity entity = prReviewHitJpaRepository
			.findByPrReviewIdAndUserId(hit.getPrReviewId(), hit.getUserId());

		return PrReviewHitMapper.toModel(entity);
	}

	@Override
	public void updateLastIncreasedAt(Long id, LocalDateTime now) {
		prReviewHitJpaRepository.updateLastIncreasedAt(id, now);
	}
}

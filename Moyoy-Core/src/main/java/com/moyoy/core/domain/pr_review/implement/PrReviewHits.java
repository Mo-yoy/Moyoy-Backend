package com.moyoy.core.domain.pr_review.implement;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.moyoy.core.common.entity.BaseTimeEntity;
import com.moyoy.core.domain.user.implement.User;

import jakarta.persistence.*;

@Entity
@Table(name = "pr_review_hits", uniqueConstraints = {
	@UniqueConstraint(name = "unique_user_pr", columnNames = {"user_id", "pr_review_id"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PrReviewHits extends BaseTimeEntity {

	// TODO: 조회수 부분은 기획 변경으로 로직이 많이 바뀔 것임.
	@Id
	@Column(name = "pr_review_hits_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pr_review_id", nullable = false)
	private PrReview prReview;

	public PrReviewHits(User user, PrReview prReview) {
		this.user = user;
		this.prReview = prReview;
	}

}

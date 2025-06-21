package com.moyo.backend.domain.pr_review.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.moyo.backend.common.entity.BaseTimeEntity;
import com.moyo.backend.domain.pr_review.domain.position.Position;
import com.moyo.backend.domain.user.implement.User;

import jakarta.persistence.*;

@Entity
@Table(name = "pr_review")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PrReview extends BaseTimeEntity {

	@Id
	@Column(name = "pr_review_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String title;

	@Lob
	@Column(nullable = false)
	@JdbcTypeCode(SqlTypes.LONGVARCHAR)
	private String content;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

	@Column(nullable = false)
	private String prUrl;

	@Column(nullable = true)
	@Enumerated(EnumType.STRING)
	private Position position;

	@Column(nullable = false)
	private int hitCount;

	@Column(nullable = false)
	private Boolean status; // open, closed 여부.

	@Builder
	public PrReview(String title, String content, User user, String prUrl, Position position, int hitCount, boolean status) {
		this.title = title;
		this.content = content;
		this.user = user;
		this.prUrl = prUrl;
		this.position = position;
		this.hitCount = hitCount;
		this.status = status;
	}

	public void increaseHitCount() {
		this.hitCount++;
	}

	public void updateDetail(String title, String content, String prUrl, Position position) {
		this.title = title;
		this.content = content;
		this.prUrl = prUrl;
		this.position = position;
	}
}

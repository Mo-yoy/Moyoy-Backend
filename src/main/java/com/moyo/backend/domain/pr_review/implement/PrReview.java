package com.moyo.backend.domain.pr_review.implement;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.moyo.backend.common.entity.BaseTimeEntity;
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
	private boolean opened; // open, closed 여부.

	@Column(nullable = false)
	private boolean adopted;

	@Builder
	public PrReview(String title, String content, User user, String prUrl, Position position, int hitCount, boolean opened, boolean adopted) {
		this.title = title;
		this.content = content;
		this.user = user;
		this.prUrl = prUrl;
		this.position = position;
		this.hitCount = hitCount;
		this.opened = opened;
		this.adopted = adopted;
	}

	public static PrReview from(
		String title,
		String position,
		String prUrl,
		String content,
		User user) {
		return PrReview.builder()
			.title(title)
			.content(content)
			.user(user)
			.prUrl(prUrl)
			.position(Position.from(position))
			.hitCount(0)
			.opened(true)
			.adopted(false)
			.build();
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

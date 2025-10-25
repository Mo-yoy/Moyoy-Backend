package com.moyoy.infra.database.mysql.pr_review;

import java.time.LocalDateTime;

import lombok.*;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.moyoy.domain.pr_review.Position;
import com.moyoy.domain.pr_review.Status;

import com.moyoy.infra.database.mysql.support.entity.BaseTimeEntity;

import jakarta.persistence.*;

@Entity
@Table(name = "pr_review")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PrReviewEntity extends BaseTimeEntity {

	@Id
	@Column(name = "pr_review_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private Long userId;

	@Column(nullable = false)
	private String title;

	@Enumerated(EnumType.STRING)
	private Position position;

	@Column(nullable = false)
	private String prUrl;

	@Lob
	@Column(nullable = false)
	@JdbcTypeCode(SqlTypes.LONGVARCHAR)
	private String content;

	@Column(nullable = false)
	private int hitCount;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private Status status;

	@Column(nullable = false)
	private boolean adopted;

	@Column(nullable = false)
	private LocalDateTime closedAt;
}

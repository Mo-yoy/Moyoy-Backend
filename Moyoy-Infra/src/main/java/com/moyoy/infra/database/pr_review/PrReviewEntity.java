package com.moyoy.infra.database.pr_review;

import com.moyoy.domain.pr_review.Position;
import com.moyoy.domain.pr_review.Status;
import com.moyoy.infra.database.support.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

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

	@Lob
	@Column(nullable = false)
	@JdbcTypeCode(SqlTypes.LONGVARCHAR)
	private String content;

	@Column(nullable = false)
	private String prUrl;

	@Column(nullable = true)
	@Enumerated(EnumType.STRING)
	private Position position;

	@Column(nullable = false)
	private int hitCount;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private Status status;

	@Column(nullable = false)
	private boolean adopted;

	@Column(nullable = false)
	private LocalDateTime deadline; // TODO: 일주일씩 추가 가능하도록 관리 필요.
}

package com.moyoy.infra.database.domain.ranking;

import com.moyoy.infra.database.support.entity.BaseTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "rankings")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RankingEntity extends BaseTimeEntity {

	@Id
	@Column(name = "ranking_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private Long userId;
	private String grade;
	private long weeklyPoint;
	private long monthlyPoint;
	private long yearlyPoint;

	@Builder
	public RankingEntity(Long id, Long userId, String grade, long weeklyPoint, long monthlyPoint, long yearlyPoint) {
		this.id = id;
		this.userId = userId;
		this.grade = grade;
		this.weeklyPoint = weeklyPoint;
		this.monthlyPoint = monthlyPoint;
		this.yearlyPoint = yearlyPoint;
	}
}

package com.moyo.backend.ranking.implement;

import com.moyo.backend.common.domain.BaseTimeEntity;
import com.moyo.backend.user.domain.User;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "rankings")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Ranking extends BaseTimeEntity {

	// 1대1 매핑에 대해서는 더 고민중임
	@Id
	@Column(name = "user_id")
	private Long userId;

	@OneToOne(fetch = FetchType.LAZY)
	@MapsId
	@JoinColumn(name = "user_id")
	private User user;

	private String grade;
	private long weeklyPoint;
	private long monthlyPoint;
	private long yearlyPoint;

	public void updateRankingByBatch(String grade, long weeklyPoint, long monthlyPoint, long yearlyPoint) {
		this.grade = grade;
		this.weeklyPoint = weeklyPoint;
		this.monthlyPoint = monthlyPoint;
		this.yearlyPoint = yearlyPoint;
	}
}

package com.moyo.backend.domain.pr_review.implement.dto;

import java.time.Duration;
import java.time.LocalDateTime;

import com.moyo.backend.domain.pr_review.implement.PrReview;

public record PrReviewSummary(
	String profileImageUrl,
	String username,
	String position,
	String title,
	String since,
	int hitCount) {
	public static PrReviewSummary from(PrReview pr) {
		return new PrReviewSummary(
			pr.getUser().getProfileImgUrl(),
			pr.getUser().getUsername(),
			pr.getPosition().toString(),
			pr.getTitle(),
			formatTimeSince(pr.getCreatedAt()),
			pr.getHitCount());
	}

	private static String formatTimeSince(LocalDateTime createdAt) {
		LocalDateTime now = LocalDateTime.now();

		// ChronoUnit 인식이 안돼서 Duration으로 대체.
		Duration duration = Duration.between(createdAt, now);

		long minutes = duration.toMinutes();
		long hours = duration.toHours();
		long days = duration.toDays();
		long months = days / 30;
		long years = days / 365;

		if (minutes < 60) {
			return minutes + "분 전";
		} else if (hours < 24) {
			return hours + "시간 전";
		} else if (days < 30) {
			return days + "일 전";
		} else if (months < 12) {
			return months + "달 전";
		} else {
			return years + "년 전";
		}
	}
}

package com.moyoy.common.util;

import java.time.Duration;
import java.time.LocalDateTime;

public class TimeSinceFormatter {

	public static String formatTimeSince(LocalDateTime createdAt) {
		LocalDateTime now = LocalDateTime.now();

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

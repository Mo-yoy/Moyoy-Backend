package com.moyo.backend.common.util;

import java.time.Duration;
import java.time.LocalDateTime;

public class TimeSinceFormatter {

	public static String formatTimeSince(LocalDateTime from) {

		Duration duration = Duration.between(from, LocalDateTime.now());
		long minutes = duration.toMinutes();

		if (minutes < 60) {

			return (minutes == 0 ? 1 : minutes) + " 분전";
		}

		long hours = duration.toHours();
		if (hours < 24) {
			return hours + " 시간 전";
		}

		long days = duration.toDays();
		return days + " 일 전";
	}
}

package com.moyoy.common.util;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ThreadUtils {

	public static void sleep(long millis) {
		String threadName = Thread.currentThread().getName();

		try {
			log.info("[{}] {}ms sleep 시작", threadName, millis);
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			log.warn("[{}] Sleep 중 인터럽트 발생", threadName, e);
			Thread.currentThread().interrupt();
			throw new RuntimeException(e);
		}
	}

	public static String currentThreadName() {
		return Thread.currentThread().getName();
	}
}

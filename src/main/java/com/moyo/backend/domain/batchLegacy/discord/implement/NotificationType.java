package com.moyo.backend.domain.batchLegacy.discord.implement;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationType {
	RANKING_BATCH_START("""
		━━━━━━━━━━━━━━━━━━━
		🚀 **[Ranking-Batch-%d] 랭킹 배치 시작!**
		━━━━━━━━━━━━━━━━━━━
		"""),
	RANKING_BATCH_END("""
		━━━━━━━━━━━━━━━━━━━
		🏁 **[Ranking-Batch-%d] 랭킹 배치 종료!**


		⏳ **소요 시간:** %s

		👥 **대상 유저:** %d명
		✅ **성공:** %d명
		❌ **실패:** %d명

		📊 **성공률:** %.2f%%
		━━━━━━━━━━━━━━━━━━━
		""");

	private final String template;

	public String format(Object... args) {
		return template.formatted(args);
	}
}

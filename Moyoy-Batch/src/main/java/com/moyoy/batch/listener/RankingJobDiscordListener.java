package com.moyoy.batch.listener;

import java.time.Duration;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.StepExecution;
import org.springframework.stereotype.Component;

import com.moyoy.infra.external.discord.DiscordClient;
import com.moyoy.infra.external.discord.dto.DiscordClientRequest;

@Slf4j
@Component
@RequiredArgsConstructor
public class RankingJobDiscordListener implements JobExecutionListener {

	private final DiscordClient discordClient;

	@Override
	public void afterJob(JobExecution jobExecution) {

		int totalRead = 0;
		int totalWrite = 0;
		int totalSkip = 0;

		for (StepExecution stepExecution : jobExecution.getStepExecutions()) {
			totalRead += (int)stepExecution.getReadCount();
			totalWrite += (int)stepExecution.getWriteCount();
			totalSkip += (int)(stepExecution.getReadSkipCount()
				+ stepExecution.getProcessSkipCount()
				+ stepExecution.getWriteSkipCount());
		}

		int success = totalWrite;
		int fail = totalSkip + (totalRead - totalWrite - totalSkip);
		double successRate = totalRead == 0 ? 0 : (success * 100.0 / totalRead);

		Duration duration = Duration.between(
			jobExecution.getStartTime(),
			jobExecution.getEndTime());

		Date today = jobExecution.getJobParameters().getDate("batchStartTime");
		String todayStr = today.toInstant()
			.atZone(ZoneId.systemDefault())
			.toLocalDate()
			.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일"));

		// 색상: 초록=성공, 빨강=실패
		int color = jobExecution.getStatus() == BatchStatus.COMPLETED ? 0x00FF00 : 0xFF0000;

		String title = "📌 랭킹 배치 결과";
		String description = String.format("""
			📅 **실행일**: %s
			🏷️ **잡 이름**: %s

			⏳ **소요 시간**
			> %02d시간 %02d분 %02d초

			👥 **처리 현황**
			- 📥 총 대상: **%d명**
			- ✅ 성공: **%d명**
			- ⚠️ 스킵: **%d명**
			- ❌ 실패: **%d명**

			📊 **성공률**: **%.2f%%**
			""",
			todayStr,
			jobExecution.getJobInstance().getJobName(),
			duration.toHours(), duration.toMinutesPart(), duration.toSecondsPart(),
			totalRead, success, totalSkip, fail, successRate);

		DiscordClientRequest.Embed embed = new DiscordClientRequest.Embed(title, description, color);

		DiscordClientRequest request = DiscordClientRequest.withEmbeds("랭킹 배치 잡 알림", List.of(embed));

		discordClient.sendNotification(request);
	}
}

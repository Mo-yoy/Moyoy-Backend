package com.moyo.backend.domain.batch.ranking.implement;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.WeekFields;
import java.util.List;

import org.springframework.stereotype.Component;

import com.moyo.backend.domain.batch.ranking.dto.GithubCommitStats;
import com.moyo.backend.domain.batch.ranking.data_access.RepoContributorStats;

@Component
public class CommitStatCalculator {

	public GithubCommitStats calculateCommitStats(List<RepoContributorStats> userRepoContributorStats) {

		// 현재 날짜 기준, 시간대 설정
		ZoneId zoneId = ZoneId.systemDefault();
		LocalDate today = LocalDate.now(zoneId);
		WeekFields weekFields = WeekFields.of(DayOfWeek.SUNDAY, 1);

		int currentWeek = today.get(weekFields.weekOfWeekBasedYear());
		int currentMonth = today.getMonthValue();
		int currentYear = today.getYear();

		int thisWeekCodeAddition = 0, thisWeekCommit = 0;
		int thisMonthCodeAddition = 0, thisMonthCommit = 0;
		int thisYearCodeAddition = 0, thisYearCommit = 0;

		for (RepoContributorStats contributor : userRepoContributorStats) {

			for (RepoContributorStats.Week week : contributor.weeks()) {

				long unixSeconds = week.weekTimeStamp();
				ZonedDateTime zonedDateTime = Instant.ofEpochSecond(unixSeconds).atZone(zoneId);

				int weekOfYear = zonedDateTime.get(weekFields.weekOfWeekBasedYear());
				int month = zonedDateTime.getMonthValue();
				int year = zonedDateTime.getYear();

				if (year == currentYear) {
					thisYearCodeAddition += week.addCodeLine();
					thisYearCommit += week.commit();

					if (month == currentMonth) {
						thisMonthCodeAddition += week.addCodeLine();
						thisMonthCommit += week.commit();

						if (weekOfYear == currentWeek) {
							thisWeekCodeAddition += week.addCodeLine();
							thisWeekCommit += week.commit();
						}
					}
				}
			}
		}

		return new GithubCommitStats(
			new GithubCommitStats.WeekStats(thisWeekCommit, thisWeekCodeAddition),
			new GithubCommitStats.MonthStats(thisMonthCommit, thisMonthCodeAddition),
			new GithubCommitStats.YearStats(thisYearCommit, thisYearCodeAddition));
	}
}

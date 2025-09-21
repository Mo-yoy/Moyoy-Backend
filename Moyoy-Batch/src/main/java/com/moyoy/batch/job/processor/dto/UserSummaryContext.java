package com.moyoy.batch.job.processor.dto;

import com.moyoy.domain.ranking.GithubCommitStats;

public record UserSummaryContext(
	RepoCandidatesContext repoCandidatesContext,
	GithubCommitStats githubCommitStats) {
}

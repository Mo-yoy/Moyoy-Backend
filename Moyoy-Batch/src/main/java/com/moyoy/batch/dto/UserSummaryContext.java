package com.moyoy.batch.dto;

import com.moyoy.domain.ranking.GithubCommitStats;

public record UserSummaryContext(
	RepoCandidatesContext repoCandidatesContext,
	GithubCommitStats githubCommitStats) {
}

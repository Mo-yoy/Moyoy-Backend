package com.moyoy.batch.job.processor.dto;

import java.util.List;

public record RepoCandidatesContext(
	UserProfileContext userProfileContext,
	List<GithubRepoDetails> candidateRepos) {
}

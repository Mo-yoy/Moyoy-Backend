package com.moyoy.batch.dto;

import java.util.List;

public record RepoCandidatesContext(
	UserProfileContext userProfileContext,
	List<GithubRepoDetails> candidateRepos
) {
}

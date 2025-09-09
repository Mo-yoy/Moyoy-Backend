package com.moyoy.api.github_follow.presentation.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record GithubFollowCommandRequest(

	@NotNull @Min(1) Integer targetGithubUserId) {
}

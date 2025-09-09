package com.moyoy.api.github_follow.presentation.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record GithubFollowDetectRequest(

	@NotBlank @Pattern(regexp = "mutual|follow-only|followed-only") String detectType,

	@Min(0) Integer lastGithubUserId,

	@Min(1) @Max(100) Integer size) {

	public GithubFollowDetectRequest {
		if (lastGithubUserId == null)
			lastGithubUserId = 0;

		if (size == null)
			size = 30;
	}
}

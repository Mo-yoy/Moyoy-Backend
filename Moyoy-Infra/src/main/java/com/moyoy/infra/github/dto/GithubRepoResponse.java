package com.moyoy.infra.github.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.ZonedDateTime;

public record GithubRepoResponse(
	Long id,

	@JsonProperty("node_id")
	String nodeId,

	String name,

	@JsonProperty("full_name")
	String fullName,

	@JsonProperty("private")
	boolean isPrivate,

	Owner owner,

	@JsonProperty("html_url")
	String htmlUrl,

	String description,
	boolean fork,

	@JsonProperty("created_at")
	ZonedDateTime createdAt,

	@JsonProperty("updated_at")
	ZonedDateTime updatedAt,

	@JsonProperty("pushed_at")
	ZonedDateTime pushedAt,

	@JsonProperty("stargazers_count")
	int stargazersCount,

	@JsonProperty("watchers_count")
	int watchersCount,

	String language,

	@JsonProperty("forks_count")
	int forksCount,

	@JsonProperty("open_issues_count")
	int openIssuesCount
) {
	public record Owner(
		String login,
		Long id,

		@JsonProperty("avatar_url")
		String avatarUrl,

		@JsonProperty("html_url")
		String htmlUrl
	) {}
}

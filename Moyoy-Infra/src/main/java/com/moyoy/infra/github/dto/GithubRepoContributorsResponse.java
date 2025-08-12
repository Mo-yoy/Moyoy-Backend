package com.moyoy.infra.github.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GithubRepoContributorsResponse(
	String login,
	long id,
	@JsonProperty("node_id")
	String nodeId,

	@JsonProperty("avatar_url")
	String avatarUrl,
	@JsonProperty("gravatar_id")
	String gravatarId,

	String url,
	@JsonProperty("html_url")
	String htmlUrl,
	@JsonProperty("followers_url")
	String followersUrl,
	@JsonProperty("following_url")
	String followingUrl,
	@JsonProperty("gists_url")
	String gistsUrl,
	@JsonProperty("starred_url")
	String starredUrl,
	@JsonProperty("subscriptions_url")
	String subscriptionsUrl,
	@JsonProperty("organizations_url")
	String organizationsUrl,
	@JsonProperty("repos_url")
	String reposUrl,
	@JsonProperty("events_url")
	String eventsUrl,
	@JsonProperty("received_events_url")
	String receivedEventsUrl,

	String type,
	@JsonProperty("user_view_type")
	String userViewType,
	@JsonProperty("site_admin")
	boolean siteAdmin,

	int contributions
) {}

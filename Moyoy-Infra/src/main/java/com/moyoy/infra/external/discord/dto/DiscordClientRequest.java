package com.moyoy.infra.external.discord.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record DiscordClientRequest(
	String content,
	String username,
	String avatar_url,
	List<Embed> embeds) {

	public static DiscordClientRequest of(String content) {
		return new DiscordClientRequest(content, null, null, null);
	}

	public static DiscordClientRequest of(String content, String username) {
		return new DiscordClientRequest(content, username, null, null);
	}

	public static DiscordClientRequest withEmbeds(String content, List<Embed> embeds) {
		return new DiscordClientRequest(content, null, null, embeds);
	}

	public record Embed(
		String title,
		String description,
		Integer color) {
	}
}

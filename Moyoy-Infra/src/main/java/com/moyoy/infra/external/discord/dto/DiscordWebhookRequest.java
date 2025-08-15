package com.moyoy.infra.external.discord.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL) // null 필드는 직렬화 시 제외
public record DiscordWebhookRequest(
	String content,
	String username,
	String avatar_url,
	List<Embed> embeds) {

	public static DiscordWebhookRequest of(String content) {
		return new DiscordWebhookRequest(content, null, null, null);
	}

	public static DiscordWebhookRequest of(String content, String username) {
		return new DiscordWebhookRequest(content, username, null, null);
	}

	public static DiscordWebhookRequest withEmbeds(String content, List<Embed> embeds) {
		return new DiscordWebhookRequest(content, null, null, embeds);
	}

	public record Embed(
		String title,
		String description,
		Integer color) {
	}
}

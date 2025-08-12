package com.moyoy.infra.discord.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL) // null 필드는 직렬화 시 제외
public record DiscordWebhookRequest(
	String content,
	String username,
	String avatar_url,
	List<Embed> embeds
) {

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
		Integer color
	) {}
}

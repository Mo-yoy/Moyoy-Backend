package com.moyoy.infra.external.discord.support;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "discord.webhook-uri")
public record DiscordProperties(
	String id,
	String token) {
}

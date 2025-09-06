package com.moyoy.infra.external.discord.feign;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.moyoy.infra.external.discord.dto.DiscordClientRequest;

@Component
public class DiscordClientImpl implements DiscordClient {

	private final DiscordFeignClient discordFeignClient;
	private final String discordWebhookId;
	private final String discordWebhookToken;

	public DiscordClientImpl(
		DiscordFeignClient discordFeignClient,
		@Value("${discord.webhook-uri.id}") String discordWebhookId,
		@Value("${discord.webhook-uri.token}") String discordWebhookToken) {

		this.discordFeignClient = discordFeignClient;
		this.discordWebhookId = discordWebhookId;
		this.discordWebhookToken = discordWebhookToken;
	}

	@Override
	public void sendNotification(DiscordClientRequest discordClientRequest) {
		discordFeignClient.sendNotification(discordWebhookId, discordWebhookToken, discordClientRequest);
	}
}

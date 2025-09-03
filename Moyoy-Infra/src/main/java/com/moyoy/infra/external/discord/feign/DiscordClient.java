package com.moyoy.infra.external.discord.feign;

import com.moyoy.infra.external.discord.dto.DiscordWebhookRequest;

public interface DiscordClient {

	void sendNotification(DiscordWebhookRequest discordWebhookRequest);
}

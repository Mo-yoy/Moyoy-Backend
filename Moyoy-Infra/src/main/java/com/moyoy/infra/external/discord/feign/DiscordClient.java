package com.moyoy.infra.external.discord.feign;

import com.moyoy.infra.external.discord.dto.DiscordClientRequest;

public interface DiscordClient {

	void sendNotification(DiscordClientRequest discordClientRequest);
}

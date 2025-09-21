package com.moyoy.infra.external.discord;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import com.moyoy.infra.external.discord.dto.DiscordClientRequest;
import com.moyoy.infra.external.discord.support.DiscordProperties;

@Component
@RequiredArgsConstructor
public class DiscordClient {

	private final DiscordApi discordApi;
	private final DiscordProperties discordProperties;

	public void sendNotification(DiscordClientRequest discordClientRequest) {
		discordApi.sendNotification(discordProperties.id(), discordProperties.token(), discordClientRequest);
	}
}

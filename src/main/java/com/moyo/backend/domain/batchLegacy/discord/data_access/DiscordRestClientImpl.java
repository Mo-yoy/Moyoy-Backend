package com.moyo.backend.domain.batchLegacy.ranking.data_access;

import static org.springframework.http.MediaType.*;

import java.util.Map;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class DiscordRestClientImpl {

	private final RestClient restClient;
	private final String discordWebhookUrl;

	public DiscordRestClientImpl(
		@Qualifier("discordRestClient") RestClient restClient,
		@Value("${discord.webhook-uri.ranking}") String discordWebhookUrl) {

		this.restClient = restClient;
		this.discordWebhookUrl = discordWebhookUrl;
	}

	public void sendAlarm(String message) {

		Map<String, String> body = Map.of("content", message);

		restClient.post()
			.uri(discordWebhookUrl)
			.contentType(APPLICATION_JSON)
			.body(body)
			.retrieve()
			.toBodilessEntity();
	}
}

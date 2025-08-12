package com.moyoy.infra.discord.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.moyoy.infra.config.DiscordFeignConfig;
import com.moyoy.infra.discord.dto.DiscordWebhookRequest;

@FeignClient(
	name = "discordWebhookClient",
	url = "https://discord.com/api/webhooks",
	configuration = DiscordFeignConfig.class
)
public interface DiscordWebhookClient {

	@PostMapping("/{id}/{token}")
	void send(
		@PathVariable String id,
		@PathVariable String token,
		@RequestBody DiscordWebhookRequest body
	);

}
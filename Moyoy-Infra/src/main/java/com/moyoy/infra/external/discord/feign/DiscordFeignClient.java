package com.moyoy.infra.external.discord.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.moyoy.infra.external.discord.dto.DiscordClientRequest;
import com.moyoy.infra.external.support.config.DiscordFeignConfig;

@FeignClient(name = "discordWebhookClient", url = "https://discord.com/api/webhooks", configuration = DiscordFeignConfig.class)
interface DiscordFeignClient {

	@PostMapping("/{id}/{token}")
	void sendNotification(
		@PathVariable String id,
		@PathVariable String token,
		@RequestBody DiscordClientRequest body);

}

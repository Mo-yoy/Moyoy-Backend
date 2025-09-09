package com.moyoy.api.github_follow.application.event;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.moyoy.infra.database.redis.follow.GithubFollowSnapshotLoader;
import com.moyoy.infra.database.redis.support.RedissonLock;

@Slf4j
@Component
@RequiredArgsConstructor
public class DetectEventHandler {

	private final GithubFollowSnapshotLoader githubFollowSnapshotLoader;

	@EventListener
	@RedissonLock(key = "'follow:' + #detectEvent.userId", waitTime = 0L, leaseTime = 30L, timeUnit = TimeUnit.MINUTES)
	public CompletableFuture<Void> handleEvent(DetectEvent detectEvent) {

		log.info("[DetectEventHandler] DetectEvent : {}", detectEvent);

		return githubFollowSnapshotLoader.load(detectEvent.userId(), detectEvent.githubUserId());
	}
}

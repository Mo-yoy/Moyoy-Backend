package com.moyo.backend.common.util;

import lombok.Getter;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;

@Getter
public class RedisHealthChecker {

    private final RedisConnectionFactory factory;

    public RedisHealthChecker(RedisConnectionFactory factory) {
        this.factory = factory;
    }

    public boolean isHealthy() {
        try (RedisConnection connection = factory.getConnection()) {
            byte[] result = connection.ping().getBytes();
            return new String(result).equalsIgnoreCase("PONG");
        } catch (Exception e) {
            return false;
        }
    }
}

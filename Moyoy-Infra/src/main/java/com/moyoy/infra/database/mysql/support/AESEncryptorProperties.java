package com.moyoy.infra.database.mysql.support;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.authorized-client.crypto")
public record AESEncryptorProperties(
	String password,
	String salt) {
}

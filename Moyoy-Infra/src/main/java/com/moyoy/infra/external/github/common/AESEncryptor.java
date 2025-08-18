package com.moyoy.infra.external.github.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.encrypt.AesBytesEncryptor;
import org.springframework.stereotype.Component;


@Component
public class AESEncryptor {

	private final AesBytesEncryptor aesBytesEncryptor;

	public AESEncryptor(
		@Value("${spring.authorized-client.crypto.password}") String password,
		@Value("${spring.authorized-client.crypto.salt}") String salt
	) {
		this.aesBytesEncryptor = new AesBytesEncryptor(password, salt);
	}

	public byte[] encrypt(byte[] plain) {
		return aesBytesEncryptor.encrypt(plain);
	}

	public byte[] decrypt(byte[] cipher) {
		return aesBytesEncryptor.decrypt(cipher);
	}
}

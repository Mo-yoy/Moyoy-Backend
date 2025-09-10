package com.moyoy.infra.database.mysql.support;

import org.springframework.security.crypto.encrypt.AesBytesEncryptor;
import org.springframework.stereotype.Component;

@Component
public class AESEncryptor {

	private final AesBytesEncryptor aesBytesEncryptor;
	private final AESEncryptorProperties encryptorProperties;

	public AESEncryptor(AESEncryptorProperties encryptorProperties) {
		this.encryptorProperties = encryptorProperties;
		this.aesBytesEncryptor = new AesBytesEncryptor(encryptorProperties.password(), encryptorProperties.salt());
	}

	public byte[] encrypt(byte[] plain) {
		return aesBytesEncryptor.encrypt(plain);
	}

	public byte[] decrypt(byte[] cipher) {
		return aesBytesEncryptor.decrypt(cipher);
	}
}

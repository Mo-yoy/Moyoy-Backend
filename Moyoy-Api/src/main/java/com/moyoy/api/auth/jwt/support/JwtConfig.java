package com.moyoy.api.auth.jwt.support;

import java.nio.charset.StandardCharsets;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import com.nimbusds.jose.util.Base64URL;

import com.moyoy.api.auth.jwt.implement.JwtProvider;

@Configuration
public class JwtConfig {

	private static final String JWT_SECRET_KEY_ID = "macKey_2025_05";

	private final SecretKey jwtSecret;

	public JwtConfig(@Value("${spring.jwt.secret}") String jwtSecret) {
		this.jwtSecret = new SecretKeySpec(jwtSecret.getBytes(StandardCharsets.UTF_8), JWSAlgorithm.HS256.getName());
	}

	@Bean
	public JwtProvider jwtProvider() throws JOSEException {
		return new JwtProvider(macSigner(), octetSequenceKey());
	}

	@Bean
	public MACSigner macSigner() throws JOSEException {
		return new MACSigner(jwtSecret);
	}

	@Bean
	public MACVerifier macVerifier() throws JOSEException {
		return new MACVerifier(jwtSecret);
	}

	@Bean
	public OctetSequenceKey octetSequenceKey() {

		byte[] secretBytes = jwtSecret.getEncoded();

		return new OctetSequenceKey.Builder(Base64URL.encode(secretBytes))
			.keyID(JWT_SECRET_KEY_ID) // for Key Rolling
			.algorithm(JWSAlgorithm.HS256)
			.build();
	}
}

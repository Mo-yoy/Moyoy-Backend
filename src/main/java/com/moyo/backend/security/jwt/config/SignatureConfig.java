package com.moyo.backend.security.jwt.config;

import com.moyo.backend.security.jwt.MacSecuritySigner;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import com.nimbusds.jose.util.Base64URL;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Configuration
public class SignatureConfig {

    private final SecretKey jwtSecret;

    public SignatureConfig(@Value("${spring.jwt.secret}") String jwtSecret) {
        this.jwtSecret = new SecretKeySpec(jwtSecret.getBytes(StandardCharsets.UTF_8), JWSAlgorithm.HS256.getName());
    }

    @Bean
    public MacSecuritySigner macSecuritySigner(){
        return new MacSecuritySigner();
    }

    @Bean
    public OctetSequenceKey octetSequenceKey() {

        byte[] secretBytes = jwtSecret.getEncoded();

        return new OctetSequenceKey.Builder(Base64URL.encode(secretBytes))
                .keyID("macKey")
                .algorithm(JWSAlgorithm.HS256)
                .build();
    }
}

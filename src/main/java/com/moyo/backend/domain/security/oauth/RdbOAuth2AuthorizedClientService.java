package com.moyo.backend.domain.security.oauth;

import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.security.oauth2.client.JdbcOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.stereotype.Component;

@Component
public class RdbOAuth2AuthorizedClientService extends JdbcOAuth2AuthorizedClientService {

	public RdbOAuth2AuthorizedClientService(JdbcOperations jdbcOperations, ClientRegistrationRepository clientRegistrationRepository) {
		super(jdbcOperations, clientRegistrationRepository);
	}

	// 추후 AccessToken Value와 RefreshToken Value에 대한 암호화를 여기서 진행
}

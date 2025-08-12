package com.moyoy.api.auth.security.component;

import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.security.oauth2.client.JdbcOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.stereotype.Component;

/**
 *   OAuth2AuthorizedClientService의 구현체, 커스텀 하지 않으면 기본값 서버 메모리 저장소
 */

@Component
public class RdbOAuth2AuthorizedClientService extends JdbcOAuth2AuthorizedClientService {

	public RdbOAuth2AuthorizedClientService(JdbcOperations jdbcOperations, ClientRegistrationRepository clientRegistrationRepository) {
		super(jdbcOperations, clientRegistrationRepository);
	}

	// TODO : 추후 AccessToken Value와 RefreshToken Value에 대한 암호화를 여기서 진행
}

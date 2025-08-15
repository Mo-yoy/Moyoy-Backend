package com.moyoy.infra.database.domain.github;

import java.util.Optional;

public interface OAuthTokenRepository {

	Optional<String> findToken(String registrationId, String principalName);
}

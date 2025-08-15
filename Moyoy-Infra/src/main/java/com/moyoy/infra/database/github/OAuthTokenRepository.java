package com.moyoy.infra.database.github;

import java.util.Optional;

public interface OAuthTokenRepository {

	Optional<String> findToken(String registrationId, String principalName);
}

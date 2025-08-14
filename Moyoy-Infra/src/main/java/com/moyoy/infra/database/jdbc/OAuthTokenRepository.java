package com.moyoy.infra.database.jdbc;

import java.util.Optional;

public interface OAuthTokenRepository {

	Optional<String> findToken(String registrationId, String principalName);
}

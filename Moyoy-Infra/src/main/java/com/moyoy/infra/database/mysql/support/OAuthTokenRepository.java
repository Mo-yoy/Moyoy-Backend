package com.moyoy.infra.database.mysql.support;

import java.util.Optional;

public interface OAuthTokenRepository {

	Optional<String> findAccessToken(String registrationId, String principalName);
}

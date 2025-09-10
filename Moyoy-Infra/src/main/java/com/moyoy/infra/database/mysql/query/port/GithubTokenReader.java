package com.moyoy.infra.database.mysql.query.port;

import java.util.Optional;

public interface GithubTokenReader {

	Optional<String> findAccessTokenWithTokenType(Long userId);
}

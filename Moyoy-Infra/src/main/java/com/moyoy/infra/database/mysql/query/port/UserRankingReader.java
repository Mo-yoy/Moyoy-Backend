package com.moyoy.infra.database.mysql.query.port;

import java.util.Optional;

import com.moyoy.infra.database.mysql.query.dto.UserRankingView;

public interface UserRankingReader {

	Optional<UserRankingView> findByUserId(Long userId);
}

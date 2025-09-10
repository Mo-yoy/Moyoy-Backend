package com.moyoy.infra.database.mysql.query;

import java.util.Optional;

public interface UserRankingQueryRepository {

	Optional<UserRankingView> findByUserId(Long userId);
}

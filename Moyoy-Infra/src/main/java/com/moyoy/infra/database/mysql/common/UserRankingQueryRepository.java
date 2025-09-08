package com.moyoy.infra.database.mysql.common;

import java.util.Optional;

public interface UserRankingQueryRepository {

	Optional<UserRankingView> findByUserId(Long userId);
}

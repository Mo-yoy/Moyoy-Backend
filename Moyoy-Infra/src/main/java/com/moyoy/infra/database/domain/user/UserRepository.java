package com.moyoy.infra.database.domain.user;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

	Optional<UserEntity> findById(Long userId);

	Optional<UserEntity> findByGithubUserId(Integer githubUserId);

	void save(UserEntity user);

	List<UserEntity> findByIdIn(List<Long> userIds);

	List<UserEntity> findAll(Long lastUserId, int size);

	UserCountAndLastId fetchUserCountAndLastId();
}

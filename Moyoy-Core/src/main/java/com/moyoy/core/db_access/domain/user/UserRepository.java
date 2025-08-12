package com.moyoy.core.db_access.domain.user;

import java.util.List;
import java.util.Optional;

import com.moyoy.core.domain.user.implement.User;
import com.moyoy.core.db_access.domain.ranking.UserCountAndLastId;

public interface UserRepository {

	Optional<User> findById(Long userId);

	Optional<User> findByGithubUserId(Integer githubUserId);

	void save(User user);

	List<User> findByIdIn(List<Long> userIds);

	List<User> findAll(Long lastUserId, int size);

	UserCountAndLastId fetchUserCountAndLastId();
}

package com.moyoy.domain.user.data_access;

import java.util.List;
import java.util.Optional;

import com.moyoy.domain.ranking.data_access.UserCountAndLastId;
import com.moyoy.domain.user.implement.User;

public interface UserRepository {

	Optional<User> findById(Long userId);

	Optional<User> findByGithubUserId(Integer githubUserId);

	void save(User user);

	List<User> findByIdIn(List<Long> userIds);

	List<User> findAll(Long lastUserId, int size);

	UserCountAndLastId fetchUserCountAndLastId();
}

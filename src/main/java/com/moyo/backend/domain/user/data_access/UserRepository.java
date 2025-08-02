package com.moyo.backend.domain.user.data_access;

import java.util.List;
import java.util.Optional;

import com.moyo.backend.domain.batchLegacy.ranking.data_access.UserCountAndLastId;
import com.moyo.backend.domain.user.implement.User;

public interface UserRepository {

	Optional<User> findById(Long userId);

	Optional<User> findByGithubUserId(Integer githubUserId);

	void save(User user);

	List<User> findByIdIn(List<Long> userIds);

	List<User> findAll(Long lastUserId, int size);

	UserCountAndLastId fetchUserCountAndLastId();
}

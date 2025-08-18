package com.moyoy.domain.user;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

	Optional<User> findById(Long userId);

	Optional<User> findByGithubUserId(Integer githubUserId);

	User save(User user);

	List<User> findByIdIn(List<Long> userIds);

	List<User> findAll(Long lastUserId, int size);

	UserFetchSummary fetchUserCountAndLastId();
}

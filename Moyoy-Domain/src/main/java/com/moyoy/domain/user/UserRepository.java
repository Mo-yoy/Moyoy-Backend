package com.moyoy.domain.user;

import java.util.Optional;

public interface UserRepository {

	Optional<User> findById(Long userId);

	Optional<User> findByGithubUserId(Integer githubUserId);

	User save(User user);
}

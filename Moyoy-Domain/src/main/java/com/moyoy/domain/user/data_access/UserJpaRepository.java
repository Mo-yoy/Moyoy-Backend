package com.moyoy.domain.user.data_access;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.moyoy.domain.user.implement.User;

public interface UserJpaRepository extends JpaRepository<User, Long> {

	Optional<User> findByGithubUserId(Integer githubUserId);

	@EntityGraph(attributePaths = "ranking")
	Page<User> findAll(Pageable pageable);
}

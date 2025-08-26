package com.moyoy.infra.database.mysql.user;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJpaRepository extends JpaRepository<UserEntity, Long> {

	Optional<UserEntity> findByGithubUserId(Integer githubUserId);

	@EntityGraph(attributePaths = "ranking")
	Page<UserEntity> findAll(Pageable pageable);
}

package com.moyo.backend.user.infrastructure;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.moyo.backend.user.domain.User;

public interface UserJpaRepository extends JpaRepository<User, Long> {

	@EntityGraph(attributePaths = "ranking")
	Page<User> findAll(Pageable pageable);
}

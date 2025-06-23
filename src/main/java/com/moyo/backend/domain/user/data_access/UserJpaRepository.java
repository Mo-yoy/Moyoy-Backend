package com.moyo.backend.domain.user.data_access;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.moyo.backend.domain.user.implement.User;

public interface UserJpaRepository extends JpaRepository<User, Long> {

	@EntityGraph(attributePaths = "ranking")
	Page<User> findAll(Pageable pageable);
}

package com.moyo.backend.domain.user.data_access;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.moyo.backend.domain.user.implement.User;

public interface UserJpaRepository extends JpaRepository<User, Long> {

	Optional<User> findByGithubUserId(Integer githubUserId);
	
	@EntityGraph(attributePaths = "ranking")
	Page<User> findAll(Pageable pageable);

	@Query("SELECT u.id FROM User u ORDER BY u.id asc ")
	List<Long> findAllUserIds();
}

package com.moyo.backend.follow.infrastructure.repository;

import com.moyo.backend.follow.domain.entity.Following;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowingJpaRepository extends JpaRepository<Following, Long> {
}

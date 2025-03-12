package com.moyo.backend.follow.infrastructure.repository;

import com.moyo.backend.follow.domain.entity.Follower;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowerJpaRepository extends JpaRepository<Follower, Long> {
}
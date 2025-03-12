package com.moyo.backend.follow.infrastructure.repository;

import com.moyo.backend.follow.domain.entity.GithubUser;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface GithubUserJpaRepository extends JpaRepository<GithubUser,Long>{
    @Query("""
    SELECT g FROM GithubUser g
    WHERE g.id IN (
        SELECT f1.githubUser.id FROM Follower f1
        INNER JOIN Following f2 ON f1.githubUser.id = f2.githubUser.id
        WHERE f1.user.id = :userId AND f2.user.id = :userId
    )
    """)
    Slice<GithubUser> findMutualFollowingGithubUsers(Long userId, Pageable pageable);

    @Query("""
    SELECT g FROM GithubUser g
    WHERE g.id IN (
        SELECT f1.githubUser.id FROM Follower f1
        LEFT JOIN Following f2 ON f1.githubUser.id = f2.githubUser.id AND f2.user.id = :userId
        WHERE f1.user.id = :userId
        AND f2.githubUser.id IS NULL
    )
""")
    Slice<GithubUser> findFollowerOnlyList(Long userId, Pageable pageable);

    @Query("""
    SELECT g FROM GithubUser g
    WHERE g.id IN (
        SELECT f2.githubUser.id FROM Following f2
        LEFT JOIN Follower f1 ON f2.githubUser.id = f1.githubUser.id AND f1.user.id = :userId
        WHERE f2.user.id = :userId
        AND f1.githubUser.id IS NULL
    )
""")
    Slice<GithubUser> findFollowingOnlyList(Long userId, Pageable pageable);

}
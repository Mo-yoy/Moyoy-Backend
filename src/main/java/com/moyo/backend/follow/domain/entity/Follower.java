package com.moyo.backend.follow.domain.entity;

import com.moyo.backend.common.entity.BaseTimeEntity;
import com.moyo.backend.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "github_follower")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Follower extends BaseTimeEntity {

    @Id
    @Column(name = "github_follower_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "github_user_id")
    private GithubUser githubUser;

    public Follower(User user, GithubUser githubUser) {
        this.user = user;
        this.githubUser = githubUser;
    }
}
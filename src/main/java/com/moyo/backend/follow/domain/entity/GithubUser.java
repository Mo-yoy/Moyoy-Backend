package com.moyo.backend.follow.domain.entity;

import com.moyo.backend.common.entity.BaseTimeEntity;
import com.moyo.backend.follow.domain.vo.GitHubUserInfo;
import com.moyo.backend.follow.infrastructure.httpClient.dto.GithubUserResponse;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "github_users")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GithubUser extends BaseTimeEntity {

    @Id
    @Column(name = "github_user_id")
    private Long id;

    @Embedded
    private GitHubUserInfo gitHubUserInfo;
}

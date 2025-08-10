package com.moyoy.domain.user.implement;

import com.moyoy.domain.common.entity.BaseTimeEntity;
import com.moyoy.domain.common.github.GithubUserProfileDto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "users")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity {

	@Id
	@Column(name = "user_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true)
	private Integer githubUserId;

	@Column(nullable = false)
	private String username;

	@Column(nullable = false)
	private String profileImgUrl;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private Role role;

	@Builder
	public User(Long id, Integer githubUserId, String username, String profileImgUrl, Role role) {
		this.id = id;
		this.githubUserId = githubUserId;
		this.username = username;
		this.profileImgUrl = profileImgUrl;
		this.role = role;
	}

	public static User from(GithubUserProfileDto githubUserProfileDto) {
		return User.builder()
			.githubUserId(githubUserProfileDto.githubUserId())
			.username(githubUserProfileDto.username())
			.profileImgUrl(githubUserProfileDto.profileImgUrl())
			.build();
	}

	public void changeUsername(String username) {
		this.username = username;
	}

	public void changeProfileImgUrl(String profileImgUrl) {
		this.profileImgUrl = profileImgUrl;
	}

	public void initRole() {
		this.role = Role.USER;
	}

}

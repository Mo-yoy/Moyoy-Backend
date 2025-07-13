package com.moyo.backend.domain.user.implement;

import static com.moyo.backend.common.constant.MoyoConstants.*;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import org.springframework.security.oauth2.core.user.OAuth2User;

import com.moyo.backend.common.entity.BaseTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Table(name = "users")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity {

	@Id
	@Column(name = "user_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String username;

	@Column(nullable = false, unique = true)
	private Integer githubUserId;

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

	@Deprecated
	public static User from(OAuth2User oAuth2User) {
		return User.builder()
			.githubUserId(Long.parseLong(oAuth2User.getAttribute(GITHUB_OAUTH2_USER_ID).toString()))
			.username(oAuth2User.getAttribute(GITHUB_OAUTH2_USER_NAME))
			.profileImgUrl(oAuth2User.getAttribute(GITHUB_OAUTH2_USER_AVATAR_URL))
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

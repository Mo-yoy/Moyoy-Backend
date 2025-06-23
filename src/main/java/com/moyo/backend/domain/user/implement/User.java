package com.moyo.backend.domain.user.implement;

import static com.moyo.backend.common.constant.MoyoConstants.*;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import org.springframework.security.oauth2.core.user.OAuth2User;

import com.moyo.backend.common.entity.BaseTimeEntity;
import com.moyo.backend.domain.github_ranking.implement.Ranking;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Table(name = "users")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity {

	// ID는 직접 관리
	@Id
	@Column(name = "user_id")
	private Long id;

	@Column(nullable = false, unique = true)
	private String username;

	@Column(nullable = false)
	private String profileImgUrl;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private Role role;

	@OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
	private Ranking ranking;

	@Builder(access = AccessLevel.PRIVATE)
	public User(Long id, String username, String profileImgUrl, Role role, Ranking ranking) {
		this.id = id;
		this.username = username;
		this.profileImgUrl = profileImgUrl;
		this.role = role;
		this.ranking = ranking;
	}

	public static User from(OAuth2User oAuth2User) {
		return User.builder()
			.id(Long.parseLong(oAuth2User.getAttribute(GITHUB_OAUTH2_USER_ID).toString()))
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

	public void changeRole(Role role) {
		this.role = role;
	}

	public void updateRanking(Ranking ranking) {
		this.ranking = ranking;
	}
}

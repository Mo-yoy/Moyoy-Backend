package com.moyoy.infra.database.user;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.moyoy.domain.user.Role;
import com.moyoy.infra.database.support.entity.BaseTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Table(name = "users")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserEntity extends BaseTimeEntity {

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
	public UserEntity(Long id, Integer githubUserId, String username, String profileImgUrl, Role role) {
		this.id = id;
		this.githubUserId = githubUserId;
		this.username = username;
		this.profileImgUrl = profileImgUrl;
		this.role = role;
	}
}

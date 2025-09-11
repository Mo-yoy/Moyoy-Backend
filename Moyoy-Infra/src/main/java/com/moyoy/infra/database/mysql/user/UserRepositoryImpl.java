package com.moyoy.infra.database.mysql.user;

import java.util.Optional;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Repository;

import com.moyoy.domain.user.User;
import com.moyoy.domain.user.UserRepository;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

	private final UserJpaRepository userJpaRepository;

	@Override
	public Optional<User> findById(Long userId) {

		Optional<UserEntity> userEntity = userJpaRepository.findById(userId);
		return userEntity.map(UserMapper::toModel);
	}

	@Override
	public Optional<User> findByGithubUserId(Integer githubUserId) {

		Optional<UserEntity> userEntity = userJpaRepository.findByGithubUserId(githubUserId);
		return userEntity.map(UserMapper::toModel);
	}

	@Override
	public User save(User user) {

		UserEntity userEntity = UserMapper.toEntity(user);
		userEntity = userJpaRepository.save(userEntity);

		return UserMapper.toModel(userEntity);
	}

}

package com.moyoy.infra.database.mysql.user;

import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Repository;

import com.moyoy.domain.user.User;
import com.moyoy.domain.user.UserFetchSummary;
import com.moyoy.domain.user.UserRepository;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

	private final UserJpaRepository userJpaRepository;
	private final UserQueryDslRepository userQueryDslRepository;

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

	@Override
	public List<User> findByIdIn(List<Long> userIds) {

		List<UserEntity> userEntityList = userJpaRepository.findAllById(userIds);
		return userEntityList.stream().map(UserMapper::toModel).toList();
	}

	@Override
	public List<User> findAll(Long lastUserId, int size) {

		List<UserEntity> userEntityList = userQueryDslRepository.findAll(lastUserId, size);
		return userEntityList.stream().map(UserMapper::toModel).toList();
	}

	@Override
	public UserFetchSummary fetchUserCountAndLastId() {

		UserEntityMetaDto userEntityMetaDto = userQueryDslRepository.fetchUserCountAndLastId();
		return new UserFetchSummary(userEntityMetaDto.userCount(), userEntityMetaDto.lastUserId());
	}

}

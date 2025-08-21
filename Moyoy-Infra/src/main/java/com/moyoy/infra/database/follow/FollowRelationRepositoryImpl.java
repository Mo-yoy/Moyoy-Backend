package com.moyoy.infra.database.follow;

import java.util.Optional;
import java.util.TreeSet;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.moyoy.domain.follow.FollowRelation;
import com.moyoy.domain.follow.FollowRelationRepository;
import com.moyoy.domain.follow.FollowUser;
import com.moyoy.domain.user.User;
import com.moyoy.domain.user.UserRepository;
import com.moyoy.infra.external.github.follow.GithubFollowFeignClient;
import com.moyoy.infra.external.github.user.GithubUserFeignClient;

@Component
@RequiredArgsConstructor
public class FollowRelationRepositoryImpl implements FollowRelationRepository {

	private final GithubFollowRelationSnapshotReader followRelationSnapshotManager;
	private final GithubFollowRelationSnapshotUpdater followRelationSnapshotUpdater;
	private final GithubUserFeignClient githubUserFeignClient;
	private final GithubFollowFeignClient githubFollowFeignClient;
	private final UserRepository userRepository;

	@Override
	public FollowRelation loadFollowRelation(Long userId, boolean forceSync, Integer githubUserId, String accessToken) {

		GithubFollowRelationSnapshot followRelationSnapshot = followRelationSnapshotManager.loadFollowRelationSnapshot(userId, forceSync, githubUserId, accessToken);

		return FollowRelation.builder()
			.userId(
				followRelationSnapshot.getUserId()
			)
			.followers(
				followRelationSnapshot.getGithubFollowers()
					.stream()
					.map(u -> new FollowUser(u.id(), u.username(), u.profileImgUrl()))
					.collect(Collectors.toCollection(TreeSet::new)))
			.followings(
				followRelationSnapshot.getGithubFollowings()
					.stream()
					.map(u -> new FollowUser(u.id(), u.username(), u.profileImgUrl()))
					.collect(Collectors.toCollection(TreeSet::new)))
			.createdAt(
				followRelationSnapshot.getCreatedAt()
			)
			.build();
	}

	@Override
	public void follow(Long currentUserId, Integer currentUserGithubId, Integer targetUserGithubId, String accessToken) {

		GithubUser currentUser = GithubUser.from(
			githubUserFeignClient.fetchUser(accessToken, currentUserGithubId));
		GithubUser targetUser = GithubUser.from(
			githubUserFeignClient.fetchUser(accessToken, targetUserGithubId));

		ResponseEntity<Void> followResult = githubFollowFeignClient.follow(targetUser.username(), accessToken);

		int responseStatus = followResult.getStatusCode().value();

		if (responseStatus == 204) {

			followRelationSnapshotUpdater.addFollowingToCurrentUser(currentUserId, targetUser);

			Optional<User> followedMoyoyUser = userRepository.findByGithubUserId(targetUserGithubId);

			if (followedMoyoyUser.isPresent()) {

				Long targetUserId = followedMoyoyUser.get().getId();
				followRelationSnapshotUpdater.addFollowerToTargetUser(targetUserId, currentUser);
			}
		}
	}

	@Override
	public void unfollow(Long currentUserId, Integer currentUserGithubId, Integer targetUserGithubId, String accessToken) {

		GithubUser currentUser = GithubUser.from(
			githubUserFeignClient.fetchUser(accessToken, currentUserGithubId));
		GithubUser targetUser = GithubUser.from(
			githubUserFeignClient.fetchUser(accessToken, targetUserGithubId));

		ResponseEntity<Void> unfollowResult = githubFollowFeignClient.unfollow(targetUser.username(), accessToken);

		int responseStatus = unfollowResult.getStatusCode().value();

		if (responseStatus == 204) {

			followRelationSnapshotUpdater.deleteFollowingToCurrentUser(currentUserId, targetUser);

			Optional<User> unfollowedMoyoyUser = userRepository.findByGithubUserId(targetUserGithubId);

			if (unfollowedMoyoyUser.isPresent()) {

				Long targetUserId = unfollowedMoyoyUser.get().getId();
				followRelationSnapshotUpdater.deleteFollowerToTargetUser(targetUserId, currentUser);
			}
		}

	}
}

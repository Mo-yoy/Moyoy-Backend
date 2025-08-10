package com.moyoy.domain.follow;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GithubFollowRelation {

	private Long userId;

	// Id ASC
	private TreeSet<GithubFollowUser> githubFollowers;
	private TreeSet<GithubFollowUser> githubFollowings;
	private LocalDateTime createdAt;

	@Builder(access = AccessLevel.PRIVATE)
	private GithubFollowRelation(Long userId, TreeSet<GithubFollowUser> githubFollowers, TreeSet<GithubFollowUser> githubFollowings, LocalDateTime createdAt) {
		this.userId = userId;
		this.githubFollowers = githubFollowers;
		this.githubFollowings = githubFollowings;
		this.createdAt = createdAt;
	}

	public static GithubFollowRelation create(Long userId, List<GithubFollowUser> githubFollowers, List<GithubFollowUser> githubFollowings) {
		return GithubFollowRelation.builder()
			.userId(userId)
			.githubFollowers(new TreeSet<>(githubFollowers))
			.githubFollowings(new TreeSet<>(githubFollowings))
			.createdAt(LocalDateTime.now())
			.build();
	}

	public List<GithubFollowUser> filterUsersByDetectType(DetectType detectType) {
		Set<GithubFollowUser> tempSet = new TreeSet<>();

		switch (detectType) {
			case MUTUAL -> {
				tempSet = new TreeSet<>(githubFollowings);
				tempSet.retainAll(githubFollowers);
			}
			case FOLLOW_ONLY -> {
				tempSet = new TreeSet<>(githubFollowings);
				tempSet.removeAll(githubFollowers);
			}
			case FOLLOWED_ONLY -> {
				tempSet = new TreeSet<>(githubFollowers);
				tempSet.removeAll(githubFollowings);
			}
		}

		return new ArrayList<>(tempSet);
	}

	public void addFollowing(GithubFollowUser user) {

		githubFollowings.add(user);
	}

	public void addFollower(GithubFollowUser user) {

		githubFollowers.add(user);
	}

	public void removeFollowing(GithubFollowUser user) {

		githubFollowings.remove(user);
	}

	public void removeFollower(GithubFollowUser user) {

		githubFollowers.remove(user);
	}

	public List<Integer> getGithubFollowingUserIds() {

		return githubFollowings.stream()
			.map(GithubFollowUser::id)
			.collect(Collectors.toList());
	}
}

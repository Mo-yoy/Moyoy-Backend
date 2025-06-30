package com.moyo.backend.domain.github_follow.implement;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GithubFollowRelation {

	private Long userId;

	// Id ASC
	private TreeSet<GithubUser> githubFollowers;
	private TreeSet<GithubUser> githubFollowings;
	private LocalDateTime createdAt;

	@Builder(access = AccessLevel.PRIVATE)
	private GithubFollowRelation(Long userId, TreeSet<GithubUser> githubFollowers, TreeSet<GithubUser> githubFollowings, LocalDateTime createdAt) {
		this.userId = userId;
		this.githubFollowers = githubFollowers;
		this.githubFollowings = githubFollowings;
		this.createdAt = createdAt;
	}

	public static GithubFollowRelation create(Long userId, List<GithubUser> githubFollowers, List<GithubUser> githubFollowings) {
		return GithubFollowRelation.builder()
			.userId(userId)
			.githubFollowers(new TreeSet<>(githubFollowers))
			.githubFollowings(new TreeSet<>(githubFollowings))
			.createdAt(LocalDateTime.now())
			.build();
	}

	public List<GithubUser> filterUsersByDetectType(DetectType detectType) {
		Set<GithubUser> tempSet = new TreeSet<>();

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

	public void addFollowing(GithubUser user) {

		githubFollowings.add(user);
	}

	public void addFollower(GithubUser user) {

		githubFollowers.add(user);
	}

	public void removeFollowing(GithubUser user) {

		githubFollowings.remove(user);
	}

	public void removeFollower(GithubUser user) {

		githubFollowers.remove(user);
	}

	public List<Long> getGithubFollowingUserIds() {

		return githubFollowings.stream()
			.map(GithubUser::id)
			.toList();
	}
}

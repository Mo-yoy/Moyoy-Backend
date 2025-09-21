package com.moyoy.infra.redis.cache.github_follow;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.moyoy.domain.github_follow.DetectType;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GithubFollowSnapshot {

	private Set<GithubUserProfile> githubFollowers;
	private Set<GithubUserProfile> githubFollowings;
	private LocalDateTime snapshotTime;

	public static GithubFollowSnapshot of(List<GithubUserProfile> githubFollowers, List<GithubUserProfile> githubFollowings, LocalDateTime snapshotTime) {

		return new GithubFollowSnapshot(
			new TreeSet<>(githubFollowers),
			new TreeSet<>(githubFollowings),
			snapshotTime);
	}

	public List<GithubUserProfile> filterByDetectType(DetectType detectType) {

		Set<GithubUserProfile> result = switch (detectType) {
			case MUTUAL -> {
				Set<GithubUserProfile> temp = new TreeSet<>(githubFollowings);
				temp.retainAll(githubFollowers);
				yield temp;
			}
			case FOLLOW_ONLY -> {
				Set<GithubUserProfile> temp = new TreeSet<>(githubFollowings);
				temp.removeAll(githubFollowers);
				yield temp;
			}
			case FOLLOWED_ONLY -> {
				Set<GithubUserProfile> temp = new TreeSet<>(githubFollowers);
				temp.removeAll(githubFollowings);
				yield temp;
			}
		};
		return new ArrayList<>(result);
	}

	public void addFollowing(GithubUserProfile user) {

		githubFollowings.add(user);
	}

	public void addFollower(GithubUserProfile user) {

		githubFollowers.add(user);
	}

	public void removeFollowing(GithubUserProfile user) {

		githubFollowings.remove(user);
	}

	public void removeFollower(GithubUserProfile user) {

		githubFollowers.remove(user);
	}

	public boolean canRefresh() {

		return snapshotTime.plusMinutes(5).isBefore(LocalDateTime.now());
	}
}

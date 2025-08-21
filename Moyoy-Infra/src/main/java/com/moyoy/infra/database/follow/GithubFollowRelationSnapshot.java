package com.moyoy.infra.database.follow;

import java.time.LocalDateTime;
import java.util.List;
import java.util.TreeSet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 *  여러가지 API 호출 후 필요한 데이터를 가공한 Github API Response Snapshot
 */

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GithubFollowRelationSnapshot {

	private Long userId;
	private TreeSet<GithubUser> githubFollowers;
	private TreeSet<GithubUser> githubFollowings;
	private LocalDateTime createdAt;

	public static GithubFollowRelationSnapshot create(Long userId, List<GithubUser> githubFollowers, List<GithubUser> githubFollowings) {
		return GithubFollowRelationSnapshot.builder()
			.userId(userId)
			.githubFollowers(new TreeSet<>(githubFollowers))
			.githubFollowings(new TreeSet<>(githubFollowings))
			.createdAt(LocalDateTime.now())
			.build();
	}

	///  캐시 오염 방지 메서드
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
}

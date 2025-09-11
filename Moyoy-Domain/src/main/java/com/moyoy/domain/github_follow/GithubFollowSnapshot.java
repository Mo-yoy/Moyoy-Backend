package com.moyoy.domain.github_follow;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/// TODO : ENTITY로 변환 OR 도메인 모델을 다시 짜야함. 원본이 깃허브에 있고 로딩이 캐시에서 돼서 조금 생각 더해 보고 설계함
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GithubFollowSnapshot {

	private Set<GithubUser> githubFollowers;
	private Set<GithubUser> githubFollowings;
	private LocalDateTime snapshotTime;

	public static GithubFollowSnapshot of(List<GithubUser> githubFollowers, List<GithubUser> githubFollowings, LocalDateTime snapshotTime) {

		return new GithubFollowSnapshot(
			new TreeSet<>(githubFollowers),
			new TreeSet<>(githubFollowings),
			snapshotTime);
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

	public boolean canRefresh() {

		return snapshotTime.plusMinutes(5).isBefore(LocalDateTime.now());
	}
}

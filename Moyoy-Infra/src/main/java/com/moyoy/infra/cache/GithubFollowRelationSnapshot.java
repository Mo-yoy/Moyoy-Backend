package com.moyoy.infra.cache;

import java.time.LocalDateTime;
import java.util.List;

import com.moyoy.domain.follow.GithubUser;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GithubFollowRelationSnapshot {

	private List<GithubUser> githubFollowers;
	private List<GithubUser> githubFollowings;
	private LocalDateTime snapshotTime;
}

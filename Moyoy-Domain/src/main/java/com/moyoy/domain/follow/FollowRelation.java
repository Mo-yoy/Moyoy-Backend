package com.moyoy.domain.follow;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import lombok.Builder;
import lombok.Getter;

/**
 *  해당 도메인 모델은 깃허브상의 데이터와 실시간 싱크를 맞추기 위함 비영속적인 특성을 지닝.
 *
 *  1. 우리 영속성 저장소에 저장 후, 짧은 주기로 폴링
 *  2. 사용자가 해당 데이터를 사용할 때 외부 API를 이용해서 실시간 데이터를 받아옴
 *
 *  1번 방식 사용시 너무 많은 리소스가 들어서 2번을 선택함. 대신, 한 번 받아온 도메인 모델은 15분간 Redis에 저장해
 *  너무 많은 외부 API 호출 제거
 */

@Getter
@Builder
public class FollowRelation {

	// FK == PK
	private Long userId;
	private TreeSet<FollowUser> followers;
	private TreeSet<FollowUser> followings;
	private LocalDateTime createdAt;

	public List<FollowUser> filterUsersByDetectType(DetectType detectType) {
		Set<FollowUser> tempSet = new TreeSet<>();

		switch (detectType) {
			case MUTUAL -> {
				tempSet = new TreeSet<>(followings);
				tempSet.retainAll(followers);
			}
			case FOLLOW_ONLY -> {
				tempSet = new TreeSet<>(followings);
				tempSet.removeAll(followers);
			}
			case FOLLOWED_ONLY -> {
				tempSet = new TreeSet<>(followers);
				tempSet.removeAll(followings);
			}
		}

		return new ArrayList<>(tempSet);
	}

	public List<Integer> getGithubFollowingUserIds() {

		return followings.stream()
			.map(FollowUser::id)
			.collect(Collectors.toList());
	}
}

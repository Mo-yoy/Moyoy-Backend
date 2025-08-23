package com.moyoy.domain.follow;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.springframework.stereotype.Component;

@Component
public class GithubFollowClassifier {

	public List<GithubUser> classifyByDetectType(DetectType detectType, List<GithubUser> followers, List<GithubUser> followings) {

		Set<GithubUser> tempSet = switch (detectType) {

			case MUTUAL -> {
				tempSet = new TreeSet<>(followings);
				tempSet.retainAll(followers);
				yield tempSet;
			}
			case FOLLOW_ONLY -> {
				tempSet = new TreeSet<>(followings);
				tempSet.removeAll(followers);
				yield tempSet;
			}
			case FOLLOWED_ONLY -> {
				tempSet = new TreeSet<>(followers);
				tempSet.removeAll(followings);
				yield tempSet;
			}
		};

		return new ArrayList<>(tempSet);
	}
}

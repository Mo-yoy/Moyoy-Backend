package com.moyoy.domain.github_follow;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.springframework.stereotype.Component;

@Component
public class GithubFollowClassifier {

	public List<GithubUser> classifyByDetectType(DetectType detectType, GithubFollowSnapshot githubFollowSnapshot) {

		Set<GithubUser> tempSet = switch (detectType) {

			case MUTUAL -> {
				tempSet = new TreeSet<>(githubFollowSnapshot.getGithubFollowings());
				tempSet.retainAll(githubFollowSnapshot.getGithubFollowers());
				yield tempSet;
			}
			case FOLLOW_ONLY -> {
				tempSet = new TreeSet<>(githubFollowSnapshot.getGithubFollowings());
				tempSet.removeAll(githubFollowSnapshot.getGithubFollowers());
				yield tempSet;
			}
			case FOLLOWED_ONLY -> {
				tempSet = new TreeSet<>(githubFollowSnapshot.getGithubFollowers());
				tempSet.removeAll(githubFollowSnapshot.getGithubFollowings());
				yield tempSet;
			}
		};

		return new ArrayList<>(tempSet);
	}
}

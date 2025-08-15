package com.moyoy.core.domain.user.business;

import org.springframework.stereotype.Service;

import com.moyoy.core.support.error.user.UserNotFoundException;
import com.moyoy.core.domain.github.GithubOAuthTokenReader;
import com.moyoy.core.domain.follow.implement.GithubUserReader;
import com.moyoy.core.domain.ranking.implement.Ranking;
import com.moyoy.core.domain.ranking.implement.RankingReader;
import com.moyoy.core.domain.user.implement.GithubUserProfileMeta;
import com.moyoy.core.domain.user.implement.User;
import com.moyoy.core.domain.user.implement.UserReader;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserReader userReader;
	private final RankingReader rankingReader;
	private final GithubUserReader githubUserReader;
	private final GithubOAuthTokenReader githubOAuthTokenReader;

	public UserProfileResult getUserProfile(Long userId) {

		User user = userReader.findById(userId).orElseThrow(UserNotFoundException::new);
		Ranking ranking = rankingReader.getRanking(user.getId());

		Integer githubUserId = user.getGithubUserId();
		String accessToken = githubOAuthTokenReader.getGithubAccessToken(userId);

		GithubUserProfileMeta githubUserProfileMeta = githubUserReader.fetchGithubUserProfile(githubUserId, accessToken);

		return UserProfileResult.from(user, ranking, githubUserProfileMeta);
	}
}

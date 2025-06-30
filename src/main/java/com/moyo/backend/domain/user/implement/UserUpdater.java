package com.moyo.backend.domain.user.implement;

import static com.moyo.backend.common.constant.MoyoConstants.*;

import lombok.RequiredArgsConstructor;

import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import com.moyo.backend.domain.github_ranking.data_access.RankingRepository;
import com.moyo.backend.domain.github_ranking.implement.Ranking;
import com.moyo.backend.domain.user.data_access.UserRepository;

import jakarta.transaction.Transactional;

@Component
@Transactional
@RequiredArgsConstructor
public class UserUpdater {

	private final UserRepository userRepository;
	private final RankingRepository rankingRepository;

	public void signUp(OAuth2User githubUser) {

		User user = User.from(githubUser);
		user.initRole();

		Ranking ranking = Ranking.initRanking(user.getId());

		userRepository.save(user);
		rankingRepository.save(ranking);
	}

	public void updateProfile(User moyoUser, OAuth2User githubUser) {

		moyoUser.changeUsername(githubUser.getAttribute(GITHUB_OAUTH2_USER_NAME));
		moyoUser.changeProfileImgUrl(githubUser.getAttribute(GITHUB_OAUTH2_USER_AVATAR_URL));
	}
}

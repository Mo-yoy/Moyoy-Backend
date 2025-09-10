package com.moyoy.infra.database.mysql.query;

import static org.assertj.core.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import com.moyoy.domain.user.Role;
import com.moyoy.domain.user.SocialSize;

import com.moyoy.infra.database.mysql.query.config.QueryDslTestConfig;
import com.moyoy.infra.database.mysql.query.dto.UserProfileView;
import com.moyoy.infra.database.mysql.ranking.RankingEntity;
import com.moyoy.infra.database.mysql.user.UserEntity;

@DataJpaTest
@Import({QueryDslTestConfig.class, UserRankingQueryDslRepository.class})
class UserRankingQueryDslRepositoryTest {

	@Autowired
	private UserRankingQueryDslRepository userRankingQueryDslRepository;

	@Autowired
	private TestEntityManager entityManager;

	@Test
	void UserId로_UserRankingView를_조회할_수_있다() {

		// given
		UserEntity user = UserEntity.builder()
			.githubUserId(1001)
			.username("moyoy")
			.profileImgUrl("https://profile.img")
			.socialSize(SocialSize.SMALL)
			.role(Role.USER)
			.build();
		entityManager.persist(user);

		RankingEntity ranking = RankingEntity.builder()
			.userId(user.getId())
			.grade("A")
			.weeklyPoint(0L)
			.monthlyPoint(0L)
			.yearlyPoint(1000L)
			.build();
		entityManager.persist(ranking);

		entityManager.flush();
		entityManager.clear();

		// when
		Optional<UserProfileView> result = userRankingQueryDslRepository.findUserRankingView(user.getId());

		// then
		assertThat(result).isPresent();
		assertThat(result.get().userId()).isEqualTo(user.getId());
		assertThat(result.get().username()).isEqualTo("moyoy");
		assertThat(result.get().yearlyRankPoint()).isEqualTo(1000L);
		assertThat(result.get().yearlyRankGrade()).isEqualTo("A");
		assertThat(result.get().profileImgUrl()).isEqualTo("https://profile.img");
	}

	@Test
	void 존재하지_않는_UserId로_조회하면_빈값을_반환한다() {

		Long notExistUserId = 9999L;

		// when
		Optional<UserProfileView> result = userRankingQueryDslRepository.findUserRankingView(notExistUserId);

		// then
		assertThat(result).isEmpty();
	}

}

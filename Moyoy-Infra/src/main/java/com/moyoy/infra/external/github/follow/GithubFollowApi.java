package com.moyoy.infra.external.github.follow;

import static com.moyoy.common.constant.MoyoConstants.*;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import com.moyoy.infra.external.github.support.GithubFeignConfig;
import com.moyoy.infra.external.github.user.dto.GithubUserResponse;

import feign.Response;

@FeignClient(name = "githubFollowClient", url = "https://api.github.com", configuration = GithubFeignConfig.class)
interface GithubFollowApi {

	/**
	 * <a href="https://docs.github.com/en/rest/users/followers?apiVersion=2022-11-28#list-followers-of-the-authenticated-user">
	 * List followers of the authenticated user
	 * </a>
	 *
	 * <p>인증된 사용자의 팔로워 목록을 조회합니다.</p>
	 *
	 * <h4>Response status codes</h4>
	 * <ul>
	 *   <li>200 OK - 요청 성공, 팔로워 목록 반환</li>
	 *   <li>304 Not Modified - 변경 없음 (캐시 사용 시)</li>
	 *   <li>401 Unauthorized - 인증 실패, 액세스 토큰 누락 또는 잘못됨</li>
	 *   <li>403 Forbidden - 요청 거부됨 (API 제한 등)</li>
	 * </ul>
	 *
	 * @param bearer GitHub OAuth Access Token ("Bearer {token}")
	 * @param perPage 한 페이지당 결과 수 (기본 30, 최대 100)
	 * @param page 페이지 번호 (1부터 시작)
	 * @return GitHub 사용자 목록
	 */
	@GetMapping("/user/followers")
	List<GithubUserResponse> fetchPagedFollowers(
		@RequestHeader(AUTHORIZATION) String bearer,
		@RequestParam("per_page") int perPage,
		@RequestParam("page") int page);

	/**
	 * <a href="https://docs.github.com/en/rest/users/followers?apiVersion=2022-11-28#list-the-people-the-authenticated-user-follows">
	 * List the people the authenticated user follows
	 * </a>
	 *
	 * <p>인증된 사용자가 팔로우하는 사용자 목록을 조회합니다.</p>
	 *
	 * <h4>Response status codes</h4>
	 * <ul>
	 *   <li>200 OK - 요청 성공, 팔로우 중인 사용자 목록 반환</li>
	 *   <li>304 Not Modified - 변경 없음 (캐시 사용 시)</li>
	 *   <li>401 Unauthorized - 인증 실패, 액세스 토큰 누락 또는 잘못됨</li>
	 *   <li>403 Forbidden - 요청 거부됨 (API 제한 등)</li>
	 * </ul>
	 *
	 * @param bearer GitHub OAuth Access Token ("Bearer {token}")
	 * @param perPage 한 페이지당 결과 수 (기본 30, 최대 100)
	 * @param page 페이지 번호 (1부터 시작)
	 * @return GitHub 사용자 목록
	 */
	@GetMapping("/user/following")
	List<GithubUserResponse> fetchPagedFollowings(
		@RequestHeader(AUTHORIZATION) String bearer,
		@RequestParam("per_page") int perPage,
		@RequestParam("page") int page);

	/**
	 * <a href="https://docs.github.com/en/rest/users/followers?apiVersion=2022-11-28#follow-a-user">
	 * Follow a user
	 * </a>
	 *
	 * <p>인증된 사용자가 지정한 유저를 팔로우합니다.</p>
	 *
	 * <h4>Response status codes</h4>
	 * <ul>
	 *   <li>204 No Content - 요청 성공, 팔로우 완료</li>
	 *   <li>304 Not Modified - 이미 해당 유저를 팔로우 중</li>
	 *   <li>401 Unauthorized - 인증 실패, 액세스 토큰 누락 또는 잘못됨</li>
	 *   <li>403 Forbidden - 요청 거부됨 (API 제한 등)</li>
	 *   <li>404 Not Found - 대상 username이 존재하지 않음</li>
	 *   <li>422 Unprocessable Entity - 검증 실패, 또는 과도한 요청으로 스팸 처리됨</li>
	 * </ul>
	 *
	 * @param bearer GitHub OAuth Access Token ("Bearer {token}")
	 * @param username 팔로우할 대상 GitHub username
	 * @return GitHub 응답(Response), 보통 본문은 없음
	 */
	@PutMapping("/user/following/{username}")
	Response follow(
		@RequestHeader(AUTHORIZATION) String bearer,
		@PathVariable("username") String username);

	/**
	 * <a href="https://docs.github.com/en/rest/users/followers?apiVersion=2022-11-28#unfollow-a-user">
	 * Unfollow a user
	 * </a>
	 *
	 * <p>인증된 사용자가 지정한 유저를 언팔로우합니다.</p>
	 *
	 * <h4>Response status codes</h4>
	 * <ul>
	 *   <li>204 No Content - 요청 성공, 언팔로우 완료</li>
	 *   <li>304 Not Modified - 이미 언팔로우 상태</li>
	 *   <li>401 Unauthorized - 인증 실패, 액세스 토큰 누락 또는 잘못됨</li>
	 *   <li>403 Forbidden - 요청 거부됨 (API 제한 등)</li>
	 *   <li>404 Not Found - 대상 username이 존재하지 않음</li>
	 * </ul>
	 *
	 * @param bearer GitHub OAuth Access Token ("Bearer {token}")
	 * @param username 언팔로우할 대상 GitHub username
	 * @return GitHub 응답(Response), 보통 본문은 없음
	 */
	@DeleteMapping("/user/following/{username}")
	Response unfollow(
		@RequestHeader(AUTHORIZATION) String bearer,
		@PathVariable("username") String username);
}

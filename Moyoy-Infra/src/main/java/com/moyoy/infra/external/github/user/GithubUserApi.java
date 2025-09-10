package com.moyoy.infra.external.github.user;

import static com.moyoy.common.constant.MoyoConstants.*;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import com.moyoy.infra.external.github.support.GithubFeignConfig;
import com.moyoy.infra.external.github.user.dto.GithubUserResponse;

import feign.Response;

@FeignClient(
	name = "githubProfileClient",
	url = "https://api.github.com",
	configuration = GithubFeignConfig.class
)
public interface GithubUserApi {

	/**
	 * <a href="https://docs.github.com/en/rest/users/users?apiVersion=2022-11-28#get-a-user-using-their-id">
	 * Get a user using their ID
	 * </a>
	 *
	 * <p>GitHub 계정의 <b>numeric ID(account_id)</b>를 사용해 해당 사용자의 공개 정보를 조회합니다.<br>
	 * (로그인 ID는 변경될 수 있지만, numeric ID는 변하지 않는 durable identifier)</p>
	 *
	 * <p>Enterprise Managed User 또는 GitHub App bot 계정 정보에 접근하려면,
	 * 해당 조직에 접근 권한이 있는 사용자 또는 GitHub App 인증이 필요합니다.
	 * 권한이 없으면 <b>404 Not Found</b>가 반환됩니다.</p>
	 *
	 * <p><b>Note:</b> 응답의 <code>email</code> 필드는 사용자가 프로필에서 공개 설정한 이메일만 노출됩니다.<br>
	 * 비공개 이메일은 null 로 반환되며, 본인의 전체 이메일 목록은 <a href="https://docs.github.com/en/rest/users/emails">Emails API</a>를 통해 확인할 수 있습니다.</p>
	 *
	 * <h4>Response status codes</h4>
	 * <ul>
	 *   <li>200 OK - 요청 성공, 사용자 정보 반환</li>
	 *   <li>404 Not Found - 리소스를 찾을 수 없음 (권한 없음 또는 계정 없음)</li>
	 * </ul>
	 *
	 * @param bearer GitHub OAuth Access Token ("Bearer {token}")
	 * @param githubUserId GitHub numeric ID (account_id)
	 * @return 사용자 공개 프로필 정보
	 */
	@GetMapping("/user/{userId}")
	GithubUserResponse fetchUser(
		@RequestHeader(AUTHORIZATION) String bearer,
		@PathVariable("userId") Integer githubUserId
	);

	/**
	 * <p>GitHub 사용자 정보를 원본 Response 형태로 반환합니다.<br>
	 * 헤더(X-RateLimit 등) 확인이나 raw body 파싱이 필요할 때 사용합니다.</p>
	 *
	 * @param bearer GitHub OAuth Access Token ("Bearer {token}")
	 * @param githubUserId GitHub numeric ID (account_id)
	 * @return GitHub API 원본 Response
	 */
	@GetMapping("/user/{userId}")
	Response fetchUserRawResponse(
		@RequestHeader(AUTHORIZATION) String bearer,
		@PathVariable("userId") Integer githubUserId
	);

}

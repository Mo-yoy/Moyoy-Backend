package com.moyoy.infra.external.github.repo;

import static com.moyoy.common.constant.MoyoConstants.*;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import com.moyoy.infra.external.github.repo.dto.GithubRepoContributorsResponse;
import com.moyoy.infra.external.github.repo.dto.GithubRepoResponse;
import com.moyoy.infra.external.github.support.GithubFeignConfig;

import feign.Response;

@FeignClient(name = "githubRepoClient", url = "https://api.github.com", configuration = GithubFeignConfig.class)
public interface GithubRepoApi {

	/**
	 * <a href="https://docs.github.com/en/rest/repos/repos?apiVersion=2022-11-28#list-repositories-for-the-authenticated-user">
	 * List repositories for the authenticated user
	 * </a>
	 *
	 * <p>인증된 사용자가 접근 권한을 가진 저장소 목록을 조회합니다.<br>
	 * (본인이 소유한 저장소, 협업자로 추가된 저장소, 소속된 조직의 저장소 포함)</p>
	 *
	 * <h4>Response status codes</h4>
	 * <ul>
	 *   <li>200 OK - 요청 성공, 저장소 목록 반환</li>
	 *   <li>304 Not Modified - 변경 없음 (캐시 사용 시)</li>
	 *   <li>401 Unauthorized - 인증 실패 (액세스 토큰 누락 또는 잘못됨)</li>
	 *   <li>403 Forbidden - 요청 거부됨 (API 제한 등)</li>
	 *   <li>422 Validation Failed - 잘못된 파라미터 조합</li>
	 * </ul>
	 *
	 * @param bearer   GitHub OAuth Access Token ("Bearer {token}")
	 * @param affiliation   저장소 소유 범위 (owner, collaborator, organization_member)
	 * @param since         지정 시점 이후 업데이트된 저장소만 조회 (ISO 8601)
	 * @param perPage       한 페이지 결과 수 (최대 100)
	 * @param page          페이지 번호 (1부터 시작)
	 * @return 저장소 목록
	 */
	@GetMapping("/user/repos")
	List<GithubRepoResponse> fetchPagedRepos(
		@RequestHeader(AUTHORIZATION) String bearer,
		@RequestParam("affiliation") String affiliation,
		@RequestParam("since") String since,
		@RequestParam("per_page") int perPage,
		@RequestParam("page") int page);

	/**
	 * <a href="https://docs.github.com/en/rest/repos/repos?apiVersion=2022-11-28#list-repository-contributors">
	 * List repository contributors
	 * </a>
	 *
	 * <p>지정된 저장소의 기여자(contributors) 목록을 조회합니다.<br>
	 * 각 기여자는 커밋 수 기준으로 내림차순 정렬되며, 데이터는 캐싱되어 최대 몇 시간 전 상태일 수 있습니다.</p>
	 *
	 * <p>GitHub는 작성자 이메일 주소를 기준으로 기여자를 식별하며, 동일 사용자 계정에 연결된 모든 이메일을 합산합니다.
	 * 성능 최적화를 위해 처음 500개의 이메일만 GitHub 사용자와 매핑되며, 나머지는 익명(anonymous) 기여자로 표시됩니다.</p>
	 *
	 * <h4>Response status codes</h4>
	 * <ul>
	 *   <li>200 OK - 요청 성공, 기여자 목록 반환</li>
	 *   <li>204 No Content - 저장소가 비어 있음</li>
	 *   <li>403 Forbidden - 접근 권한 없음</li>
	 *   <li>404 Not Found - 리소스를 찾을 수 없음</li>
	 * </ul>
	 *
	 * @param bearer  GitHub OAuth Access Token ("Bearer {token}")
	 * @param repoFullName 저장소 전체 이름 ("owner/repo" 형식, 대소문자 구분 없음)
	 * @return 기여자 목록 (GithubRepoContributorsResponse)
	 */
	@GetMapping("/repos/{fullName}/contributors")
	List<GithubRepoContributorsResponse> fetchRepoContributors(
		@RequestHeader(AUTHORIZATION) String bearer,
		@PathVariable("fullName") String repoFullName);

	/**
	 * <a href="https://docs.github.com/en/rest/metrics/statistics?apiVersion=2022-11-28#get-all-contributor-commit-activity">
	 * Get all contributor commit activity
	 * </a>
	 *
	 * <p>지정된 저장소의 각 기여자(contributor)별 전체 커밋 활동 통계를 조회합니다.<br>
	 * 응답에는 총 커밋 수와 함께 주차별(weeks array) 데이터가 포함됩니다.</p>
	 *
	 * <ul>
	 *   <li><b>w</b> - 주 시작 시점 (Unix timestamp)</li>
	 *   <li><b>a</b> - 추가된 라인 수 (additions)</li>
	 *   <li><b>d</b> - 삭제된 라인 수 (deletions)</li>
	 *   <li><b>c</b> - 커밋 수 (commits)</li>
	 * </ul>
	 *
	 * <p><b>Note:</b> 저장소의 총 커밋 수가 10,000개 이상이면 additions/deletions 값은 0으로 반환됩니다.</p>
	 *
	 * <h4>Response behavior</h4>
	 * <ul>
	 *   <li>처음 호출 시 <b>202 Accepted</b> 와 비어 있는 본문을 반환합니다.</li>
	 *   <li>GitHub에서 통계 계산을 마친 뒤 재호출하면 <b>200 OK</b> 와 실제 데이터가 반환됩니다.</li>
	 *   <li>따라서 특정 DTO로 바로 파싱하면 202 응답 시 에러가 발생할 수 있습니다.</li>
	 *   <li>우선 <code>String</code> 으로 본문을 받고, <code>Reader</code> 에서 필요한 데이터만 직접 추출하는 방식을 권장합니다.</li>
	 * </ul>
	 *
	 * <h4>Response status codes</h4>
	 * <ul>
	 *   <li>200 OK - 요청 성공, 기여자 활동 데이터 반환</li>
	 *   <li>202 Accepted - 데이터 계산 중 (빈 본문 반환)</li>
	 *   <li>204 No Content - 저장소에 커밋 데이터 없음</li>
	 * </ul>
	 *
	 * @param bearer  GitHub OAuth Access Token ("Bearer {token}")
	 * @param repoFullName 저장소 전체 이름 ("owner/repo" 형식, 대소문자 구분 없음)
	 * @return GitHub 응답 원본 (String으로 받고 Reader에서 파싱 필요)
	 */
	@GetMapping("/repos/{repoFullName}/stats/contributors")
	Response fetchContributorCommitActivity(
		@RequestHeader(AUTHORIZATION) String bearer,
		@PathVariable("repoFullName") String repoFullName);

}

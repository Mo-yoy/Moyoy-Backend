package com.moyoy.infra.database.jwt;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

/**
 * Redis를 현재 글로벌 캐시로 활용 중이나,
 * Redis 클러스터까지 도입할 비용이 없어 단일 장애지점을 제거하기 위해
 * JWT Refresh Token 화이트리스트는 RDB(JDBC)로 관리.
 *
 * JWT Refresh Token WhiteList 엔티티.
 *
 * - DB 테이블(jwt_refresh_token)에 매핑되는 모델
 */
@Getter
@Builder
public class JwtRefreshToken {

	/**
	 * 고유 식별자 (PK, AUTO_INCREMENT)
	 * - 한 사용자 다중 기기 로그인 허용 + InnoDB 클러스터링 인덱스를 위해 별도의 Auto Increment id 추가
	 */
	private Long id;

	/**
	 * 토큰 소유자 ID
	 * - 토큰이 어떤 사용자의 것인지 매핑하기 위함
	 * - 특정 유저의 토큰 조회/청소를 빠르게 하기 위해 인덱싱
	 */
	private Long userId;

	/**
	 * 토큰 해시값 (SHA-256 → Base64 인코딩)
	 * - 원문 대신 해시를 저장하여 보안성 확보
	 * - 화이트리스트 체크 시, 입력 토큰을 동일하게 해시화하여 비교
	 * - DB 컬럼: VARCHAR(44), Base64 결과는 항상 44자
	 */
	private String tokenHash;

	/**
	 * 토큰 만료 시각
	 * - 화이트리스트 유효성 판단에 사용
	 * - 배치/스케줄러에서 만료된 토큰을 주기적으로 삭제
	 */
	private LocalDateTime expiresAt;
}

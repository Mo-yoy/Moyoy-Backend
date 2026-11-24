# 조회수 관리 로컬 측정 환경

요청글 상세 조회 API의 조회수 관리 방식(v1~v4)을 같은 조건으로 비교하기 위한 로컬 측정 환경입니다.
MySQL·Redis 자원을 프리티어 수준으로 제한했고, 수치는 절대 처리량이 아닌 **버전 간 상대 비교**용입니다.

## 실행 순서
```bash
# 1. MySQL·Redis 실행
docker compose -f perf/hit/docker-compose.yml up -d

# 2. API 서버 실행 (local 프로필, ddl-auto로 테이블 생성)
DB_URL='jdbc:mysql://localhost:3306/moyoy' DB_USERNAME=root DB_PASSWORD=root JWT_SECRET_KEY=<32자 이상> ... ./gradlew :Moyoy-Api:bootRun

# 3. 더미 데이터 (회원 5,000명, 요청글 100개)
docker exec -i moyoy-perf-mysql mysql -uroot -proot moyoy < perf/hit/seed.sql

# 4. 워밍업 1회 후 측정 (서버와 같은 JWT_SECRET_KEY 사용)
JWT_SECRET_KEY=<위와 동일> ./perf/hit/measure.sh v1 hot 200 60s
```

## 시나리오
| 시나리오 | 내용 |
|---|---|
| `hot` | 조회의 80%가 인기글 5개에 집중 |
| `spread` | 요청글 100개에 고르게 분산 |
| `refresh` | 회원 200명이 같은 글을 반복 조회 (중복 증가 방지 확인) |

회원당 분당 30회 처리율 제한(`RateLimitingFilter`)이 있으므로 `refresh`는 RATE 50 이하로 실행합니다.

## 수집 항목
- k6: p95/p99 응답 시간, 에러율 (`results/<버전>/`에 저장)
- MySQL: 측정 전후 `Com_insert`, `Com_update`, `Com_select` 증가량, 조회수 합계
- `hitsExecutor`: 처리한 조회수 작업 수, 남은 큐 크기 (actuator)

#!/usr/bin/env bash
# k6 실행 전후로 DB 쓰기 횟수, 조회수 합계, hitsExecutor 지표를 수집한다.
# 사용법: JWT_SECRET_KEY=... ./measure.sh <버전> <시나리오> [RATE] [DURATION]
set -euo pipefail

VERSION=${1:?버전 (예: v1)}
SCENARIO=${2:?시나리오 (hot|spread|refresh)}
RATE=${3:-200}
DURATION=${4:-60s}
ACTUATOR=${ACTUATOR:-http://localhost:9090/actuator/metrics}
RESULT_DIR="$(dirname "$0")/results/${VERSION}"
mkdir -p "$RESULT_DIR"

mysql_query() {
	docker exec moyoy-perf-mysql mysql -uroot -proot -N -s moyoy -e "$1" 2>/dev/null
}

db_snapshot() {
	mysql_query "SHOW GLOBAL STATUS WHERE Variable_name IN ('Com_insert', 'Com_update', 'Com_select');"
	echo "hit_count_sum $(mysql_query 'SELECT COALESCE(SUM(hit_count), 0) FROM pr_reviews;')"
}

executor_metric() {
	curl -s "${ACTUATOR}/$1?tag=name:hitsExecutor" | sed -n 's/.*"value":\([0-9.E]*\).*/\1/p'
}

# 매 측정마다 조회수와 중복 판단 기록을 초기화해 같은 조건에서 시작
mysql_query "UPDATE pr_reviews SET hit_count = 0;"
mysql_query "DELETE FROM pr_review_hits;" || true
docker exec moyoy-perf-redis redis-cli FLUSHDB > /dev/null

BEFORE=$(db_snapshot)
COMPLETED_BEFORE=$(executor_metric executor.completed)

k6 run --quiet \
	-e SCENARIO="$SCENARIO" -e RATE="$RATE" -e DURATION="$DURATION" -e JWT_SECRET_KEY="$JWT_SECRET_KEY" \
	--summary-export "${RESULT_DIR}/${SCENARIO}-k6.json" \
	"$(dirname "$0")/hit.js" | tee "${RESULT_DIR}/${SCENARIO}-k6.txt"

# 비동기 조회수 작업이 큐에서 모두 빠질 때까지 대기
for _ in $(seq 1 60); do
	QUEUED=$(executor_metric executor.queued)
	[[ "${QUEUED:-0}" == "0.0" || "${QUEUED:-0}" == "0" ]] && break
	sleep 1
done

AFTER=$(db_snapshot)

{
	echo "version=${VERSION} scenario=${SCENARIO} rate=${RATE}/s duration=${DURATION}"
	echo "--- DB (after - before)"
	join <(echo "$BEFORE" | sort) <(echo "$AFTER" | sort) | awk '{ printf "%s %d\n", $1, $3 - $2 }'
	echo "--- hitsExecutor"
	echo "completed $(awk -v a="$(executor_metric executor.completed)" -v b="$COMPLETED_BEFORE" 'BEGIN { printf "%d", a - b }')"
	echo "queued $(executor_metric executor.queued)"
} | tee "${RESULT_DIR}/${SCENARIO}-db.txt"

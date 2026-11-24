// 요청글 상세 조회 부하 시나리오 (조회수 관리 버전별 비교용)
//   SCENARIO=hot     : 조회의 80%가 인기글 5개에 몰림
//   SCENARIO=spread  : 요청글 100개에 고르게 분산
//   SCENARIO=refresh : 회원 200명이 같은 글을 반복 조회 (중복 증가 방지 확인)
// 회원당 분당 30회 처리율 제한(RateLimitingFilter)이 있으므로 refresh는 RATE 50 이하로 실행
import http from 'k6/http';
import { check } from 'k6';
import crypto from 'k6/crypto';
import encoding from 'k6/encoding';

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';
const JWT_SECRET_KEY = __ENV.JWT_SECRET_KEY;
const SCENARIO = __ENV.SCENARIO || 'hot';
const RATE = Number(__ENV.RATE || 200);
const DURATION = __ENV.DURATION || '60s';

const USER_COUNT = 5000;
const REVIEW_COUNT = 100;
const HOT_REVIEW_COUNT = 5;
const REFRESH_USER_COUNT = 200;

export const options = {
	scenarios: {
		[SCENARIO]: {
			executor: 'constant-arrival-rate',
			rate: RATE,
			timeUnit: '1s',
			duration: DURATION,
			preAllocatedVUs: 50,
			maxVUs: 300,
		},
	},
	summaryTrendStats: ['avg', 'p(50)', 'p(95)', 'p(99)', 'max'],
};

function base64Url(value) {
	return encoding.b64encode(value, 'rawurl');
}

// JwtProvider와 동일한 형태의 HS256 Access Token
function createAccessToken(userId) {
	const header = base64Url(JSON.stringify({ kid: 'macKey_2025_08', alg: 'HS256' }));
	const payload = base64Url(JSON.stringify({
		id: userId,
		type: 'access',
		authority: 'ROLE_USER',
		exp: Math.floor(Date.now() / 1000) + 3600,
	}));
	const signature = crypto.hmac('sha256', JWT_SECRET_KEY, `${header}.${payload}`, 'base64rawurl');
	return `${header}.${payload}.${signature}`;
}

function randomInt(max) {
	return Math.floor(Math.random() * max) + 1;
}

function pick() {
	switch (SCENARIO) {
		case 'spread':
			return { userId: randomInt(USER_COUNT), reviewId: randomInt(REVIEW_COUNT) };
		case 'refresh':
			return { userId: randomInt(REFRESH_USER_COUNT), reviewId: 1 };
		default:
			return {
				userId: randomInt(USER_COUNT),
				reviewId: Math.random() < 0.8 ? randomInt(HOT_REVIEW_COUNT) : randomInt(REVIEW_COUNT),
			};
	}
}

export function setup() {
	if (!JWT_SECRET_KEY) {
		throw new Error('JWT_SECRET_KEY 환경변수가 필요합니다.');
	}
	const tokens = {};
	for (let userId = 1; userId <= USER_COUNT; userId++) {
		tokens[userId] = createAccessToken(userId);
	}
	return tokens;
}

export default function (tokens) {
	const { userId, reviewId } = pick();
	const res = http.get(`${BASE_URL}/api/v1/pr-reviews/${reviewId}`, {
		headers: { Authorization: `Bearer ${tokens[userId]}` },
		tags: { name: 'GET /api/v1/pr-reviews/{id}' },
	});
	check(res, { 'status is 200': (r) => r.status === 200 });
}

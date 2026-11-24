-- 측정용 더미 데이터: 회원 5,000명, 요청글 100개
SET SESSION cte_max_recursion_depth = 10000;

INSERT INTO users (github_user_id, username, profile_img_url, social_size, role, created_at, modified_at)
WITH RECURSIVE seq(n) AS (SELECT 1 UNION ALL SELECT n + 1 FROM seq WHERE n < 5000)
SELECT n, CONCAT('perf-user-', n), 'https://example.com/profile.png', 'SMALL', 'USER', NOW(6), NOW(6)
FROM seq;

INSERT INTO pr_reviews (user_id, title, position, pr_url, content, hit_count, status, adopted, closed_at, created_at, modified_at)
WITH RECURSIVE seq(n) AS (SELECT 1 UNION ALL SELECT n + 1 FROM seq WHERE n < 100)
SELECT n, CONCAT('perf-review-', n), 'BACKEND', 'https://github.com/Mo-yoy/Moyoy-Backend/pull/1',
       REPEAT('content ', 200), 0, 'OPEN', FALSE, DATE_ADD(NOW(6), INTERVAL 30 DAY), NOW(6), NOW(6)
FROM seq;

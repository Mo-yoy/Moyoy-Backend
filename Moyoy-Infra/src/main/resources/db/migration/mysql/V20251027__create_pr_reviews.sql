CREATE TABLE pr_reviews (
    pr_review_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    position ENUM('BACKEND', 'FRONTEND', 'IOS', 'ANDROID', 'DEVOPS', 'OTHER') NULL,
    pr_url VARCHAR(255) NOT NULL,
    content LONGTEXT NOT NULL,
    hit_count INT NOT NULL,
    status ENUM('OPEN', 'CLOSED') NOT NULL,
    adopted BOOLEAN NOT NULL,
    closed_at DATETIME(6) NOT NULL,
    created_at DATETIME(6) NULL,
    modified_at DATETIME(6) NULL,
    CONSTRAINT fk_pr_review_user FOREIGN KEY (user_id) REFERENCES users(user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

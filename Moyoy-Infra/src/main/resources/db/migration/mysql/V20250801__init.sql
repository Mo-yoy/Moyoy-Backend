USE `moyoy-test`;

DROP TABLE IF EXISTS rankings; # ranking에 user_id FK
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS oauth2_client_registration;
DROP TABLE IF EXISTS jwt_access_token;

CREATE TABLE users (
    user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    github_user_id INT NOT NULL,
    username VARCHAR(255) NOT NULL,
    profile_img_url VARCHAR(255) NOT NULL,
    role ENUM ('ADMIN', 'USER') NOT NULL,
    created_at DATETIME(6) NULL,
    modified_at DATETIME(6) NULL,
    CONSTRAINT UK_github_user UNIQUE (github_user_id)
);

CREATE TABLE rankings
(
    ranking_id    BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id       BIGINT       NULL,
    monthly_point BIGINT       NOT NULL,
    weekly_point  BIGINT       NOT NULL,
    yearly_point  BIGINT       NOT NULL,
    grade         VARCHAR(255) NULL,
    created_at    DATETIME(6)  NULL,
    modified_at   DATETIME(6)  NULL,
    CONSTRAINT fk_rankings_user
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

CREATE TABLE oauth2_authorized_client
(
    client_registration_id  VARCHAR(100)   NOT NULL,
    principal_name          VARCHAR(200)   NOT NULL,
    access_token_type       VARCHAR(100)   NOT NULL,
    access_token_value      BLOB           NOT NULL,
    access_token_issued_at  TIMESTAMP      NOT NULL,
    access_token_expires_at TIMESTAMP      NOT NULL,
    access_token_scopes     VARCHAR(1000)  NULL,
    refresh_token_value     BLOB           NULL,
    refresh_token_issued_at TIMESTAMP      NULL,
    PRIMARY KEY (client_registration_id, principal_name)
);

CREATE TABLE jwt_refresh_token
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id    BIGINT      NOT NULL,
    token_hash VARCHAR(44) NOT NULL,
    expires_at TIMESTAMP   NOT NULL,
    CONSTRAINT uq_token_hash UNIQUE (token_hash)
);

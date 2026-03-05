-- 테스트 데이터: admin 1명 + 일반 유저 10,000명 (비밀번호: test1234)
SET SESSION cte_max_recursion_depth = 10001;

INSERT IGNORE INTO members (email, password, name, phone_number, role, status, is_deleted, created_at, updated_at)
VALUES ('admin@test.com', '$2b$10$g2u54HMb50bE47pn7fRXKukYIBayhc6GnjxHT9PAYk25Y0D9J45Ma', 'admin', '010-0000-0000', 'ADMIN', 'ACTIVE', false, NOW(), NOW());

INSERT IGNORE INTO members (email, password, name, phone_number, role, status, is_deleted, created_at, updated_at)
SELECT
    CONCAT('user', n, '@test.com'),
    '$2b$10$g2u54HMb50bE47pn7fRXKukYIBayhc6GnjxHT9PAYk25Y0D9J45Ma',
    CONCAT('user', n),
    CONCAT('010-', SUBSTRING(LPAD(n, 8, '0'), 1, 4), '-', SUBSTRING(LPAD(n, 8, '0'), 5, 4)),
    'USER',
    'ACTIVE',
    false,
    NOW(),
    NOW()
FROM (
    WITH RECURSIVE nums AS (
        SELECT 1 AS n
        UNION ALL
        SELECT n + 1 FROM nums WHERE n < 10000
    )
    SELECT n FROM nums
) t;

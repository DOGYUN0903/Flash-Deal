-- ========================================
-- Test Data for Flash Deal MVP
-- ========================================

-- 테스트용 상품 (Deal 대상 상품)
INSERT IGNORE INTO product (product_id, name, description, price, stock_quantity, status, is_deleted, created_at, updated_at)
VALUES (1, '한정판 스니커즈', '선착순 한정 판매 스니커즈', 200000, 100, 'ON_SALE', false, NOW(), NOW());

-- 테스트용 Deal (재고 100개, 현재 시간부터 24시간 후까지 오픈)
INSERT IGNORE INTO deal (deal_id, product_id, deal_price, stock, open_time, end_time, deleted_at, created_at, updated_at)
VALUES (1, 1, 99000, 100, NOW(), DATE_ADD(NOW(), INTERVAL 24 HOUR), NULL, NOW(), NOW());

-- 테스트용 유저 1,000명 (각 잔액 10억)
INSERT IGNORE INTO member (email, password, name, phone_number, role, status, balance, is_deleted, created_at, updated_at)
WITH RECURSIVE seq AS (
    SELECT 1 AS n
    UNION ALL
    SELECT n + 1 FROM seq WHERE n < 1000
)
SELECT
    CONCAT('user', n, '@test.com'),
    '$2a$10$dXJ3SW6G7P50lGmMQoeRNOoxCIg5AT42gLu5sCkDFgB7st6giSH5a',
    CONCAT('TestUser', n),
    CONCAT('010-0000-', LPAD(n, 4, '0')),
    'USER',
    'ACTIVE',
    1000000000,
    false,
    NOW(),
    NOW()
FROM seq;

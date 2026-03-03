-- ========================================
-- Test Data for Flash Deal MVP
-- ========================================

-- 테스트용 상품 (카테고리별 100개씩, 총 700개)
-- 상품명: 10개 베이스 x 10개 옵션 조합
INSERT IGNORE INTO products (product_id, name, description, price, stock_quantity, status, category, is_deleted, created_at, updated_at)
WITH RECURSIVE seq AS (
    SELECT 1 AS n
    UNION ALL
    SELECT n + 1 FROM seq WHERE n < 100
)
SELECT
    cat.id_offset + seq.n,
    CONCAT(
        ELT(MOD(seq.n - 1, 10) + 1, cat.p1, cat.p2, cat.p3, cat.p4, cat.p5, cat.p6, cat.p7, cat.p8, cat.p9, cat.p10),
        ' ',
        ELT(FLOOR((seq.n - 1) / 10) + 1, cat.v1, cat.v2, cat.v3, cat.v4, cat.v5, cat.v6, cat.v7, cat.v8, cat.v9, cat.v10)
    ),
    cat.base_desc,
    (MOD(seq.n * 7, cat.price_steps) + 1) * cat.price_unit + cat.base_price,
    MOD(seq.n * 3 + 7, 91) + 10,
    IF(MOD(seq.n, 10) = 0, 'SOLD_OUT', 'ON_SALE'),
    cat.code,
    false,
    DATE_SUB(NOW(), INTERVAL seq.n DAY),
    DATE_SUB(NOW(), INTERVAL seq.n DAY)
FROM seq
CROSS JOIN (
    SELECT
        'ELECTRONICS' AS code, 0 AS id_offset,
        50000 AS base_price, 50000 AS price_unit, 38 AS price_steps,
        '갤럭시 S25'                  AS p1,
        'LG 그램 노트북 16"'          AS p2,
        '에어팟 프로 4세대'            AS p3,
        'WH-1000XM5 헤드폰'           AS p4,
        '갤럭시 탭 S10 Ultra'         AS p5,
        '삼성 QLED TV 65"'            AS p6,
        '다이슨 V15 무선청소기'        AS p7,
        '갤럭시 워치 7 Pro'            AS p8,
        '캐논 EOS R50 미러리스'        AS p9,
        '맥북 에어 M3'                AS p10,
        '256GB 블랙'   AS v1,
        '512GB 실버'   AS v2,
        '128GB 화이트' AS v3,
        '1TB 그레이'   AS v4,
        '256GB 네이비' AS v5,
        '512GB 골드'   AS v6,
        '128GB 그린'   AS v7,
        '1TB 퍼플'     AS v8,
        '256GB 레드'   AS v9,
        '512GB 블루'   AS v10,
        '최신 기술이 집약된 프리미엄 전자기기입니다. 탁월한 성능과 세련된 디자인으로 일상을 더욱 스마트하게 만들어 드립니다.' AS base_desc

    UNION ALL SELECT
        'FASHION', 100,
        20000, 10000, 28,
        '나이키 에어맥스 270 스니커즈',
        '리바이스 511 슬림 청바지',
        '유니클로 후드 집업 스웨트셔츠',
        '폴로 랄프로렌 피케 셔츠',
        '자라 더블 브레스티드 트렌치코트',
        'H&M 레귤러핏 린넨 셔츠',
        '아디다스 SST 트랙 재킷',
        '컨버스 척 테일러 올스타 하이',
        'MLB 뉴욕양키스 볼캡',
        '무신사 스탠다드 슬림 슬랙스',
        'S / 화이트',
        'M / 블랙',
        'L / 네이비',
        'XL / 그레이',
        'XXL / 카키',
        'S / 베이지',
        'M / 브라운',
        'L / 머스타드',
        'XL / 올리브',
        'XXL / 버건디',
        '트렌디한 디자인과 편안한 착용감을 동시에 만족시키는 패션 아이템입니다. 다양한 코디에 활용 가능한 데일리 필수템입니다.'

    UNION ALL SELECT
        'FOOD', 200,
        5000, 3000, 25,
        '제주 한라봉',
        '국내산 햇사과',
        '1+ 등급 한우 갈비',
        '완도 활전복',
        '유기농 방울토마토',
        '경북 샤인머스켓',
        '제주 은갈치',
        '국내산 생표고버섯',
        '강원도 햇감자',
        '통영 굴',
        '1kg 선물세트',
        '2kg 가정용',
        '3kg 대용량',
        '500g 소포장',
        '5kg 대량구매',
        '1.5kg 실속포장',
        '2.5kg 혼합구성',
        '4kg 특가세트',
        '800g 프리미엄',
        '1.2kg 신선포장',
        '엄선된 산지에서 직접 공수한 신선한 국내산 식품입니다. 엄격한 품질 기준으로 선별된 최상급 상품만을 제공합니다.'

    UNION ALL SELECT
        'SPORTS', 300,
        30000, 10000, 47,
        '나이키 에어줌 페가수스 40',
        '아디다스 울트라부스트 23',
        '뉴발란스 574 클래식',
        '요가매트 6mm 논슬립',
        '조정식 덤벨 20kg 세트',
        '언더아머 HG 압박 레깅스',
        '블랙다이아몬드 트레킹폴',
        '스피도 어드밴스드 수경',
        '벨로 에어로 사이클링 헬멧',
        '줄넘기 속도줄 프로',
        '블랙 / 화이트',
        '블루 / 화이트',
        '레드 / 블랙',
        '그레이 / 블랙',
        '네이비 / 화이트',
        '올리브 / 블랙',
        '핑크 / 화이트',
        '옐로우 / 블랙',
        '민트 / 화이트',
        '오렌지 / 블랙',
        '최고의 퍼포먼스를 위한 전문 스포츠 용품입니다. 전문 선수들도 선택한 검증된 품질로 운동 효과를 극대화합니다.'

    UNION ALL SELECT
        'BEAUTY', 400,
        15000, 5000, 37,
        '설화수 자음생 크림',
        '이니스프리 그린티 씨드 세럼',
        '에스티로더 어드밴스드 나이트 리페어',
        '랑콤 어드밴스드 제니피크 세럼',
        '헤라 블랙쿠션 SPF34',
        '나스 쉬어글로우 파운데이션',
        '맥 루비 우 립스틱',
        '클라랑스 더블 세럼',
        '아모레퍼시픽 UV 선크림 SPF50+',
        'SK-II 페이셜 트리트먼트 에센스',
        '30ml',
        '50ml',
        '75ml',
        '100ml',
        '15ml 2개입 기획',
        '30ml 기획 2종',
        '50ml 여행용 세트',
        '100ml 리미티드',
        '미니어처 세트',
        '풀사이즈 스페셜 에디션',
        '피부 전문가들이 추천하는 프리미엄 뷰티 제품입니다. 섬세한 성분 설계로 건강하고 빛나는 피부를 만들어 드립니다.'

    UNION ALL SELECT
        'FURNITURE', 500,
        50000, 50000, 29,
        '이케아 KALLAX 4칸 수납 선반',
        '한샘 링크 1200 일자형 책상',
        '일룸 에피 싱글 침대 프레임',
        '이케아 포앙 암체어',
        '까사미아 블리스 3인 패브릭 소파',
        '한샘 슬라이딩 4도어 옷장',
        '이케아 LACK 사이드테이블',
        '리바트 에디 서재 3단 책장',
        '이케아 프리헤텐 코너 소파베드',
        '시디즈 T50 매쉬 사무용 의자',
        '화이트',
        '내추럴 오크',
        '블랙',
        '월넛 브라운',
        '라이트 오크',
        '미드 그레이',
        '다크 그레이',
        '비치',
        '체리',
        '마호가니',
        '공간을 아름답게 완성하는 고품질 가구입니다. 탁월한 내구성과 세련된 디자인으로 오랫동안 함께할 수 있습니다.'

    UNION ALL SELECT
        'BOOKS', 600,
        12000, 1000, 23,
        '클린 코드',
        '자바 ORM 표준 JPA 프로그래밍',
        '이펙티브 자바',
        '대규모 시스템 설계 기초',
        '도메인 주도 설계',
        '객체지향의 사실과 오해',
        '스프링 부트와 AWS 웹 서비스',
        '코딩 인터뷰 완전 분석',
        '함께 자라기',
        '데이터 중심 애플리케이션 설계',
        '(초판 1쇄)',
        '(개정 2판)',
        '(3판 전면개정)',
        '(최신 개정판)',
        '(리뉴얼 에디션)',
        '(E-Book 포함)',
        '(강의 제공)',
        '(부록 포함)',
        '(스터디 가이드)',
        '(특별판)',
        '실무 개발자들의 필독서입니다. 현장에서 바로 적용 가능한 지식과 깊이 있는 인사이트를 담았습니다.'
) cat;

-- 테스트용 관리자 계정 (admin@test.com / test1234)
INSERT IGNORE INTO members (email, password, name, phone_number, role, status, is_deleted, created_at, updated_at)
VALUES ('admin@test.com', '$2a$10$dXJ3SW6G7P50lGmMQoeRNOoxCIg5AT42gLu5sCkDFgB7st6giSH5a', '관리자', '010-0000-0000', 'ADMIN', 'ACTIVE', false, NOW(), NOW());

-- 테스트용 유저 10,000명 (password: test1234)
-- 재귀 깊이 1,000 제한을 피하기 위해 h(0~99) x t(0~99) CROSS JOIN으로 10,000개 생성
INSERT IGNORE INTO members (email, password, name, phone_number, role, status, is_deleted, created_at, updated_at)
WITH RECURSIVE
    h AS (SELECT 0 AS n UNION ALL SELECT n + 1 FROM h WHERE n < 99),
    t AS (SELECT 0 AS n UNION ALL SELECT n + 1 FROM t WHERE n < 99)
SELECT
    CONCAT('user', (h.n * 100 + t.n + 1), '@test.com'),
    '$2a$10$dXJ3SW6G7P50lGmMQoeRNOoxCIg5AT42gLu5sCkDFgB7st6giSH5a',
    CONCAT('TestUser', (h.n * 100 + t.n + 1)),
    CONCAT('010-', LPAD(h.n + 1, 4, '0'), '-', LPAD(t.n + 1, 4, '0')),
    'USER',
    'ACTIVE',
    false,
    NOW(),
    NOW()
FROM h CROSS JOIN t;

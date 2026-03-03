# Flash Deal ERD 정리

## Member (members)

| 한국어 | 영어 (컬럼명) | 타입 | NULL 여부 |
|--------|--------------|------|-----------|
| 회원 ID | member_id | BIGINT | NOT NULL (PK) |
| 이메일 | email | VARCHAR | NOT NULL (UNIQUE) |
| 비밀번호 | password | VARCHAR | NOT NULL |
| 이름 | name | VARCHAR | NOT NULL |
| 전화번호 | phone_number | VARCHAR | NOT NULL |
| 역할 | role | VARCHAR | NOT NULL |
| 상태 | status | VARCHAR | NOT NULL |
| 삭제 여부 | is_deleted | BOOLEAN | NOT NULL |
| 생성일시 | created_at | DATETIME | NOT NULL |
| 수정일시 | updated_at | DATETIME | NOT NULL |

> **role**: `USER`, `ADMIN`
> **status**: `ACTIVE`, `DORMANT`, `WITHDRAWN`, `BANNED`

---

## Product (products)

| 한국어 | 영어 (컬럼명) | 타입 | NULL 여부 |
|--------|--------------|------|-----------|
| 상품 ID | product_id | BIGINT | NOT NULL (PK) |
| 상품명 | name | VARCHAR | NOT NULL |
| 상품 설명 | description | LONGTEXT | NOT NULL |
| 가격 | price | INT | NOT NULL |
| 재고 수량 | stock_quantity | INT | NOT NULL |
| 상태 | status | VARCHAR | NOT NULL |
| 이미지 URL | image_url | VARCHAR | NULL |
| 카테고리 | category | VARCHAR | NOT NULL |
| 삭제 여부 | is_deleted | BOOLEAN | NOT NULL |
| 생성일시 | created_at | DATETIME | NOT NULL |
| 수정일시 | updated_at | DATETIME | NOT NULL |

> **status**: `PREPARING`, `ON_SALE`, `SOLD_OUT`
> **category**: `ELECTRONICS`, `FASHION`, `FOOD`, `SPORTS`, `BEAUTY`, `FURNITURE`, `BOOKS`

---

## CartItem (cart_items)

| 한국어 | 영어 (컬럼명) | 타입 | NULL 여부 |
|--------|--------------|------|-----------|
| 장바구니 항목 ID | cart_item_id | BIGINT | NOT NULL (PK) |
| 회원 ID | member_id | BIGINT | NOT NULL (FK → members) |
| 상품 ID | product_id | BIGINT | NOT NULL (FK → products) |
| 수량 | quantity | INT | NOT NULL |
| 담을 당시 단가 | price | INT | NOT NULL |
| 생성일시 | created_at | DATETIME | NOT NULL |
| 수정일시 | updated_at | DATETIME | NOT NULL |

---

## Order (orders)

| 한국어 | 영어 (컬럼명) | 타입 | NULL 여부 |
|--------|--------------|------|-----------|
| 주문 ID | order_id | BIGINT | NOT NULL (PK) |
| 회원 ID | member_id | BIGINT | NOT NULL (FK → members) |
| 주문 상태 | status | VARCHAR | NOT NULL |
| 총 결제금액 | total_price | INT | NOT NULL |
| 생성일시 | created_at | DATETIME | NOT NULL |
| 수정일시 | updated_at | DATETIME | NOT NULL |

> **status**: `PENDING`, `PAID`, `SHIPPED`, `DELIVERED`, `CANCELED`

---

## OrderItem (order_items)

| 한국어 | 영어 (컬럼명) | 타입 | NULL 여부 |
|--------|--------------|------|-----------|
| 주문 항목 ID | order_item_id | BIGINT | NOT NULL (PK) |
| 주문 ID | order_id | BIGINT | NOT NULL (FK → orders) |
| 상품 ID | product_id | BIGINT | NOT NULL (FK → products) |
| 수량 | quantity | INT | NOT NULL |
| 주문 당시 단가 | price | INT | NOT NULL |
| 주문 금액 | order_price | INT | NOT NULL |
| 생성일시 | created_at | DATETIME | NOT NULL |
| 수정일시 | updated_at | DATETIME | NOT NULL |

> **order_price** = price × quantity

---

## Payment (payments)

| 한국어 | 영어 (컬럼명) | 타입 | NULL 여부 |
|--------|--------------|------|-----------|
| 결제 ID | payment_id | BIGINT | NOT NULL (PK) |
| 주문 ID | order_id | BIGINT | NOT NULL (FK → orders, UNIQUE) |
| 결제 상태 | status | VARCHAR | NOT NULL |
| 결제 수단 | method | VARCHAR | NULL |
| 결제 금액 | amount | INT | NOT NULL |
| 결제 완료일시 | paid_at | DATETIME | NULL |
| 생성일시 | created_at | DATETIME | NOT NULL |
| 수정일시 | updated_at | DATETIME | NOT NULL |

> **status**: `PENDING`, `COMPLETED`, `FAILED`, `REFUNDED`
> **method**: `CARD`, `CASH`, `TRANSFER`, `TOSS`

---

## Review (reviews)

| 한국어 | 영어 (컬럼명) | 타입 | NULL 여부 |
|--------|--------------|------|-----------|
| 리뷰 ID | review_id | BIGINT | NOT NULL (PK) |
| 상품 ID | product_id | BIGINT | NOT NULL (FK → products) |
| 회원 ID | member_id | BIGINT | NOT NULL (FK → members) |
| 별점 | rating | INT | NOT NULL |
| 내용 | content | VARCHAR(500) | NULL |
| 생성일시 | created_at | DATETIME | NOT NULL |
| 수정일시 | updated_at | DATETIME | NOT NULL |

> **rating**: 1 ~ 5

---

## 테이블 관계 요약

| 관계 | 카디널리티 |
|------|-----------|
| Member → CartItem | 1 : N |
| Member → Order | 1 : N |
| Member → Review | 1 : N |
| Product → CartItem | 1 : N |
| Product → OrderItem | 1 : N |
| Product → Review | 1 : N |
| Order → OrderItem | 1 : N |
| Order → Payment | 1 : 1 |

# 🚀 Project Flash-Deal: MVP Specification

> **Project Goal:** > 대규모 트래픽(Traffic Spike) 상황에서 발생하는 **동시성 이슈(Concurrency Issue)**와 **DB 병목(Bottleneck)** 현상을 재현하고,
> 이를 단계적으로 해결하며 시스템 성능을 개선하는 과정을 데이터로 증명하기 위한 선착순 이커머스 프로젝트.

---

## 1. 🛠️ Tech Stack & Architecture

**MVP 단계에서는 Redis, Kafka 등 고성능 인프라를 배제하고, 모든 부하를 RDB(MySQL)와 WAS에 집중시킨다.**

* **Architecture:** Monolithic (Single Instance)
* **Language:** Java 17
* **Framework:** Spring Boot 3.x.x
* **Database:** MySQL 8.0 (Docker)
* **ORM:** Spring Data JPA
* **Auth:** Spring Security + **HttpSession (JSESSIONID)**
    * *Intent:* 추후 Scale-out 시 세션 불일치 문제를 재현하기 위함. 기존 JWT 관련 코드는 비활성화 또는 삭제.
* **Monitoring:** Prometheus + Grafana + Spring Actuator

---

## 2. 🏛️ Domain Model (Entity Requirements)

### A. Member (회원) - Internal Wallet
* **Modification:**
    * `balance` (Long): 내부 결제용 포인트/예치금 잔액 필드 추가.
    * `address` (Embedded): 선착순 구매 시 배송지 입력 시간을 단축하기 위한 필수 필드.
* **Role:** 외부 PG사 연동 대신 내부 포인트 시스템을 사용하여, 외부 네트워크 변수 없이 순수 서버 성능을 테스트한다.

### B. Deal (★ Key Domain) - New Entity
* **Definition:** 특정 시간 동안 한정된 수량을 판매하는 **선착순 이벤트** 엔티티.
* **Fields:**
    * `id` (PK)
    * `product` (ManyToOne): 대상 상품(기존 Product 엔티티 활용).
    * `dealPrice` (Integer): 할인가.
    * `stock` (Integer): **딜 전용 재고 (동시성 이슈의 핵심 지점).**
    * `openTime` / `endTime` (LocalDateTime): 판매 기간.
    * `deletedAt` (LocalDateTime): Soft Delete 적용.

### C. Order (주문) - Modification
* **Fields:**
    * `dealId` (Long, Nullable): 선착순 주문일 경우 `Deal` ID 저장.
    * `status`: `PENDING` -> `PAID` -> `CANCELLED`.

### D. Cart (장바구니 정책)
* **Policy:** `Deal` 상품(한정 판매)은 장바구니에 담을 수 없다.
* **Validation:** 장바구니 추가 요청 시 `Deal` 상품 여부를 확인하고 예외(`CustomException`)를 발생시킨다.

---

## 3. ⚡ Core Business Logic & Flow

### 3-1. Flash Deal Purchase (선착순 구매) - ★ Critical Path
**"바로 구매" 버튼 클릭 시의 프로세스. (장바구니 X)**

1.  **Gate Validation:**
    * 현재 시간이 `openTime` 이전이면 요청 거부 (`NotOpenException`).
2.  **Concurrency Control (DB Lock):**
    * **JPA Pessimistic Lock (`SELECT ... FOR UPDATE`)**을 사용하여 `Deal` 레코드를 잠근다.
    * *Intent:* 동시 접속자 증가 시 **DB Connection Pool 고갈(Exhaustion)**을 유발한다.
3.  **Mock Payment (Latency Simulation):**
    * **Module:** `FakePaymentClient` (Component) 구현.
    * **Latency:** `Thread.sleep(500 ~ 1000)`을 사용하여 외부 PG사 통신 지연을 흉내 낸다.
    * **Chaos:** 20% 확률로 결제 실패(잔액 부족 등) 예외를 발생시킨다.
    * *Effect:* 결제 지연 시간 동안 DB Lock을 계속 점유하게 되어 병목 현상을 극대화한다.
4.  **Transaction:**
    * `재고 차감` -> `Mock 결제(지연)` -> `주문 생성` -> `포인트 차감`
    * 위 모든 과정은 **하나의 트랜잭션(@Transactional)**으로 묶여야 한다.

---

## 4. 🐳 Infrastructure & Data Init

### 4-1. Docker Compose
* **MySQL 8.0:** Port 3306, Volume Mount.
* **Prometheus:** Port 9090.
* **Grafana:** Port 3000.

### 4-2. Test Data (data.sql)
* 서버 실행 시 자동으로 테스트 데이터를 로드한다.
* **User:** 테스트용 유저 1,000명 생성 (각 `balance` 10억 충전).
* **Product/Deal:** 테스트용 딜 생성 (`stock` 100개).

---

## 5. 🧪 Performance Test Scenarios (Goal)

**"성공적인 구현"이란 "부하 테스트 시 서버가 처참하게 느려지는 것"을 의미한다.**

* **Tool:** JMeter
* **Scenario:** Deal Stock 100개 / Concurrent Users 1,000명
* **Verification Points (기대 결과):**
    1.  **Data Consistency:** 재고가 정확히 0개가 되는가?
    2.  **Latency:** P99 Latency가 **3초 이상** 발생하는가? (Lock 대기 + Mock Delay)
    3.  **Error:** Connection Timeout 에러가 발생하는가?
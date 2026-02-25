# Flash Deal MVP 테스트 계획서

## 테스트 환경

| 항목 | 값 |
|------|-----|
| 서버 | NCP S2-G3 (Ubuntu, vCPU 2, 8GB RAM) |
| 브랜치 | `dev` (기능 테스트), `v1/single-server` (부하 테스트) |
| Base URL | `http://{SERVER_IP}:8080` |
| 테스트 계정 | `user1@test.com` / `test1234` (일반 유저) |
| 어드민 계정 | `admin@test.com` / `test1234` |

---

## 1. 기능 테스트 (수동 — Postman 또는 curl)

### 1-1. 인증 (Auth)

| # | API | 테스트 케이스 | 기대 결과 |
|---|-----|-------------|----------|
| 1 | `POST /api/auth/signup` | 정상 회원가입 | 201, 토큰 반환 |
| 2 | `POST /api/auth/signup` | 중복 이메일 | 400, 에러 메시지 |
| 3 | `POST /api/auth/signup` | 이메일 형식 오류 | 400, 유효성 에러 |
| 4 | `POST /api/auth/login` | 정상 로그인 | 200, JSESSIONID 쿠키 발급 |
| 5 | `POST /api/auth/login` | 잘못된 비밀번호 | 401, 에러 메시지 |
| 6 | `POST /api/auth/login` | 존재하지 않는 이메일 | 401, 에러 메시지 |
| 7 | `POST /api/auth/logout` | 로그인 상태에서 로그아웃 | 200, 세션 무효화 |
| 8 | `POST /api/auth/logout` | 미로그인 상태 | 401 |

```bash
# 로그인 예시
curl -c cookies.txt -X POST http://{SERVER_IP}:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"user1@test.com","password":"test1234"}'
```

---

### 1-2. 딜 (Deal)

| # | API | 테스트 케이스 | 기대 결과 |
|---|-----|-------------|----------|
| 9 | `GET /api/deals` | 딜 목록 조회 (페이징) | 200, 딜 리스트 |
| 10 | `GET /api/deals?page=0&size=5` | 페이지 사이즈 지정 | 200, 5개 반환 |
| 11 | `GET /api/deals/{id}` | 존재하는 딜 상세 조회 | 200, 딜 상세 |
| 12 | `GET /api/deals/{id}` | 존재하지 않는 딜 ID | 404 |
| 13 | `POST /api/deals/{id}/purchase` | 정상 구매 (로그인 상태, 재고 있음) | 201, 주문 정보 |
| 14 | `POST /api/deals/{id}/purchase` | 미로그인 상태 | 401 |
| 15 | `POST /api/deals/{id}/purchase` | 재고 0인 딜 | 400, 재고 부족 에러 |
| 16 | `POST /api/deals/{id}/purchase` | 오픈 시간 전인 딜 | 400, 딜 미오픈 에러 |
| 17 | `POST /api/deals/{id}/purchase` | 종료된 딜 | 400, 딜 미오픈 에러 |

```bash
# 딜 구매 예시 (로그인 후 쿠키 사용)
curl -b cookies.txt -X POST http://{SERVER_IP}:8080/api/deals/1/purchase
```

---

### 1-3. 주문 (Order)

| # | API | 테스트 케이스 | 기대 결과 |
|---|-----|-------------|----------|
| 18 | `GET /api/orders` | 내 주문 목록 | 200, 주문 리스트 |
| 19 | `GET /api/orders/{id}` | 내 주문 단건 조회 | 200, 주문 상세 |
| 20 | `GET /api/orders/{id}` | 타인 주문 접근 | 403 |
| 21 | `DELETE /api/orders/{id}` | 주문 취소 (PENDING 상태) | 200 |
| 22 | `DELETE /api/orders/{id}` | 이미 결제 완료된 주문 취소 | 400, 에러 |
| 23 | `POST /api/orders/direct` | 상품 바로 구매 | 201, 주문 정보 |

---

### 1-4. 결제 (Payment)

> **주의**: 실제 토스페이먼츠 연동이라 결제 승인은 테스트 키로만 가능

| # | API | 테스트 케이스 | 기대 결과 |
|---|-----|-------------|----------|
| 24 | `POST /api/payments/toss/confirm` | 유효한 paymentKey, orderId, amount | 200, 결제 승인 |
| 25 | `POST /api/payments/toss/confirm` | 금액 불일치 | 400, 에러 |
| 26 | `GET /api/payments/{id}` | 결제 단건 조회 | 200 |
| 27 | `GET /api/payments?orderId={id}` | 주문 ID로 결제 조회 | 200 |
| 28 | `POST /api/payments/{id}/refund` | 환불 처리 | 200 |

---

### 1-5. 회원 (Member)

| # | API | 테스트 케이스 | 기대 결과 |
|---|-----|-------------|----------|
| 29 | `GET /api/members/me` | 내 프로필 조회 | 200, 회원 정보 |
| 30 | `GET /api/members/me` | 미로그인 상태 | 401 |
| 31 | `PATCH /api/members/me` | 이름, 전화번호 수정 | 200, 수정된 정보 |
| 32 | `POST /api/members/me/verify-password` | 현재 비밀번호 확인 (일치) | 200 |
| 33 | `POST /api/members/me/verify-password` | 현재 비밀번호 확인 (불일치) | 400 |
| 34 | `PATCH /api/members/me/password` | 비밀번호 변경 | 200 |

---

### 1-6. 어드민 (Admin)

| # | API | 테스트 케이스 | 기대 결과 |
|---|-----|-------------|----------|
| 35 | `POST /api/admin/deals` | 딜 생성 (어드민 계정) | 201, 딜 정보 |
| 36 | `POST /api/admin/deals` | 일반 유저 계정으로 요청 | 403 |

```json
// 딜 생성 요청 바디 예시
{
  "productId": 1,
  "dealPrice": 99000,
  "stock": 100,
  "openTime": "2026-02-22T10:00:00",
  "endTime": "2026-02-23T10:00:00"
}
```

---

## 2. E2E 시나리오 테스트

### 시나리오 A: 딜 구매 전체 플로우 (Happy Path)

```
[사용자]
  1. POST /api/auth/login         → JSESSIONID 발급
  2. GET  /api/deals              → 딜 목록 확인
  3. GET  /api/deals/1            → 딜 상세 확인 (가격, 재고, 오픈시간)
  4. POST /api/deals/1/purchase   → 구매 요청 → orderId, amount 반환
  5. (프론트) 토스페이먼츠 결제창 이동
  6. POST /api/payments/toss/confirm → 결제 승인 → 주문 PAID 상태 변경
  7. GET  /api/orders/{orderId}   → 주문 상태 PAID 확인

확인 사항:
  - deal.stock이 1 감소했는지 DB에서 확인
  - order.status = PAID 확인
  - payment 레코드 생성 확인
```

---

### 시나리오 B: 재고 품절 처리

```
[사전 조건] 재고 = 1개인 딜

  1. 사용자 A: POST /api/deals/1/purchase → 성공 (재고 0)
  2. 사용자 B: POST /api/deals/1/purchase → 400, "재고가 부족합니다"

확인 사항:
  - deal.stock = 0 (음수가 되면 안 됨 ← V1에서 이게 깨짐)
```

---

### 시나리오 C: 오픈 전 딜 구매 시도

```
  1. openTime = 내일인 딜 생성
  2. POST /api/deals/{id}/purchase → 400, "딜이 오픈되지 않았습니다"

  확인 사항: 오픈 시간 전 차단 정상 동작
```

---

## 3. 부하 테스트 (V1 — 문제 재현)

> **브랜치**: `v1/single-server`으로 서버 배포 필요
> **목표**: 문제를 의도적으로 재현하고 증거를 수집한다

---

### 부하 테스트 사전 준비

```bash
# 1. 재고 리셋 (매 테스트 전 실행)
curl -b admin-cookies.txt -X POST \
  "http://{SERVER_IP}:8080/api/admin/deals/1/reset?stock=100"

# 2. Grafana 대시보드 열기
# http://{SERVER_IP}:3000 (admin/admin)

# 3. 딜 재고 현재 상태 확인
curl http://{SERVER_IP}:8080/api/deals/1
```

---

### 부하 테스트 시나리오 1: Race Condition (재고 정합성)

**목적**: 락 없이 동시 요청 시 재고가 음수가 됨을 증명

| 항목 | 값 |
|------|----|
| 대상 API | `POST /api/deals/1/purchase` |
| 동시 사용자 | 200명 |
| Ramp-up | 5초 (빠르게 동시에 부딪혀야 함) |
| 재고 | 100개 |

```bash
# JMeter 실행
jmeter -n -t load-test/v1-5000users.jmx \
  -Jusers=200 -Jrampup=5 \
  -l docs/v1/race-condition-result.jtl
```

**수집할 증거:**

```sql
-- 테스트 후 DB에서 재고 확인
SELECT stock FROM deal WHERE deal_id = 1;
-- 기대: 음수 값 → Race Condition 증명
```

- [ ] DB `stock` 음수 값 스크린샷
- [ ] Grafana `deal.stock.remaining` 게이지 0 이하로 꺾이는 그래프
- [ ] JMeter 에러 없이 200명 전부 성공 (음수 재고인데도 성공 = 문제)

---

### 부하 테스트 시나리오 2: DB 커넥션 풀 고갈

**목적**: FakePaymentClient(750ms)가 TX 안에 있어 커넥션을 점유 → Pool 고갈

| 항목 | 값 |
|------|----|
| 대상 API | `POST /api/deals/1/purchase` |
| 동시 사용자 | 100명 |
| Ramp-up | 10초 |
| HikariCP pool-size | 10 (의도적 제한) |

**이론값:**
```
최대 처리량 = pool-size(10) / 결제시간(0.75s) ≈ 13 TPS
→ 14번째 요청부터 커넥션 대기 발생
→ 100명 동시 = 87명이 30초 대기 후 타임아웃
```

```bash
jmeter -n -t load-test/v1-5000users.jmx \
  -Jusers=100 -Jrampup=10 \
  -l docs/v1/pool-exhaustion-result.jtl
```

**수집할 증거:**

- [ ] Grafana `hikaricp_connections_pending` 급등 그래프
- [ ] Grafana `hikaricp_connections_active` = 10 (풀 꽉 참)
- [ ] Grafana HTTP P99 응답시간 > 30초 그래프
- [ ] JMeter Aggregate Report (에러율, 평균 응답시간, Throughput)

---

### 부하 테스트 시나리오 3: CPU 포화

**목적**: 2 vCPU 한계 + App/DB/모니터링 경쟁 증명

| 항목 | 값 |
|------|----|
| 대상 API | `POST /api/deals/1/purchase` |
| 동시 사용자 | 500명 |
| Ramp-up | 60초 |

**수집할 증거:**

- [ ] Grafana `system_cpu_usage` → 100% 도달 그래프
- [ ] JMeter 에러율 급등
- [ ] Grafana JVM 메모리 (`jvm_memory_used`) 상승 추이

---

### 부하 테스트 결과 정리 (docs/v1/에 저장)

```
docs/v1/
├── screenshots/
│   ├── race-condition-db.png          # DB stock 음수
│   ├── race-condition-gauge.png       # Grafana 재고 게이지
│   ├── pool-exhaustion-pending.png    # HikariCP pending 급등
│   ├── pool-exhaustion-p99.png        # P99 > 30s
│   └── cpu-saturation.png            # CPU 100%
├── race-condition-result.jtl
├── pool-exhaustion-result.jtl
└── jmeter-aggregate-report.html       # JMeter → Generate HTML Report
```

---

## 4. 개선 포인트 요약 (V2에서 해결할 것)

| 문제 | 원인 | V2 해결 방법 |
|------|------|-------------|
| 재고 음수 (Race Condition) | `findById()` 락 없음 | 비관적 락 (`SELECT FOR UPDATE`) |
| 커넥션 풀 고갈 | FakePaymentClient가 TX 안에 있음 | TX 2단계 분리 (결제 TX 밖으로) |
| CPU 포화 | 2vCPU에 모든 것이 집중 | 서버 3대로 스케일아웃 |
| N+1 쿼리 | `deal.getProduct()` 지연 로딩 | `JOIN FETCH` 쿼리 |
| 인덱스 부재 | `deleted_at`, `open_time`, `end_time` 인덱스 없음 | `@Table(indexes = {...})` 추가 |
| 중복 구매 허용 | 동일 회원 + 딜 중복 체크 없음 | `existsByMemberIdAndDealId()` 체크 |

---

## 5. 체크리스트 요약

### 기능 테스트 완료 기준
- [ ] 로그인/로그아웃 정상 동작
- [ ] 딜 조회 (목록/상세) 정상 동작
- [ ] 딜 구매 → 주문 생성 → 결제 승인 플로우 정상 동작
- [ ] 재고 0 시 구매 차단 동작 (단, 동시 요청 시는 음수 발생 — 의도된 버그)
- [ ] 어드민 딜 생성 정상 동작
- [ ] 인증 필요 API 미로그인 시 401 반환

### 부하 테스트 완료 기준
- [ ] Race Condition: DB stock 음수 확인
- [ ] 커넥션 풀 고갈: Grafana pending 급등 + P99 > 30s 확인
- [ ] CPU 포화: Grafana CPU 100% 확인
- [ ] 증거 스크린샷 `docs/v1/screenshots/` 저장
- [ ] JMeter HTML 리포트 생성

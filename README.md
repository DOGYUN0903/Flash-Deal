# Flash Deal

> 선착순 한정 이커머스 플랫폼 — MAU 1만에서 500만까지의 성장 과정에서 발생하는 성능 병목을 단계적으로 해결하는 포트폴리오 프로젝트입니다.

---

## 목차

1. [프로젝트 소개](#1-프로젝트-소개)
2. [기술 스택](#2-기술-스택)
3. [아키텍처](#3-아키텍처)
4. [ERD](#4-erd)
5. [사용자 플로우](#5-사용자-플로우)
6. [실행 방법](#6-실행-방법)

---

## 1. 프로젝트 소개

Flash Deal은 **선착순 한정 이커머스 플랫폼**입니다.

단순한 기능 구현을 넘어, 서비스가 성장하면서 마주치는 **실제 성능 병목을 직접 재현하고, 여러 기술을 비교·분석한 뒤 트레이드오프를 고려하여 해결책을 선택하는 과정**을 담았습니다.

### 서비스 성장 시나리오

| 단계 | MAU | DAU | 피크 동시접속 | 핵심 이슈 |
|:----:|:---:|:---:|:-----------:|:--------:|
| MVP  | 10,000 | 1,000 | — | 기본 이커머스 구축 |
| V1   | 100,000 | 10,000 | 5,000명 | Race Condition, DB Pool 고갈 |
| V2   | 1,000,000 | 100,000 | 30,000명 | 한정판 딜 도입, 세션 불일치 |
| V3   | 5,000,000 | 500,000 | 100,000명 | Cascading Failure, 알림 부하 |

### 브랜치 구조

```
dev                  ← 통합 브랜치 (현재)
├── v1/single-server ← Race Condition + DB Pool 고갈 재현     [예정]
├── v2/scale-out     ← 비관적 락 + TX 분리 + Redis 세션       [예정]
└── v3/msa           ← Redis 재고 + Kafka 알림 + Circuit Breaker [예정]
```

---

## 2. 기술 스택

### Backend

| 분류 | 기술 |
|------|------|
| Language / Framework | Java 17, Spring Boot 3.5 |
| ORM | Spring Data JPA, QueryDSL |
| Security | Spring Security (세션 인증) |
| DB | MySQL 8.0, HikariCP |
| Monitoring | Prometheus, Micrometer, Grafana |
| API Docs | SpringDoc OpenAPI (Swagger) |

### Frontend

| 분류 | 기술 |
|------|------|
| Framework | Next.js 14 (App Router), TypeScript |
| Styling | Tailwind CSS, shadcn/ui |
| 결제 | Toss Payments SDK |

### Infrastructure

| 분류 | 기술 |
|------|------|
| Container | Docker, Docker Compose |
| Server | NCP S3-g3 (vCPU 2코어 / RAM 8GB) |
| Load Test | Apache JMeter |

---

## 3. 아키텍처

### MVP — 단일 서버

```
                ┌─────────────────────────────┐
[Client]  ───▶  │   Spring Boot App  :8080    │
                │   MySQL 8.0        :3306    │
                │   Prometheus       :9090    │
                │   Grafana          :3000    │
                └─────────────────────────────┘
                   NCP S3-g3 (2vCPU / 8GB RAM)
```

---

## 4. ERD

### MVP ERD

![Flash Deal MVP ERD](images/flash-deal_MVP_ERD.png)

---

## 5. 사용자 플로우

### 일반 구매 플로우

```
회원가입 / 로그인
        │
        ▼
상품 탐색  ── 카테고리 필터
        │    └─ 가격 범위 / 품절 제외 필터
        ▼
장바구니 담기
        │
        ▼
주문 / 결제 (Toss Payments)
        │
        ▼
리뷰 작성
```

### 주문 상태 흐름

```
PENDING ── 결제 완료 ──▶ PAID ── 배송 시작 ──▶ SHIPPED ── 배송 완료 ──▶ DELIVERED
   │                     │
   └── 취소 ──▶ CANCELED  └── 취소 ──▶ CANCELED
```

### 결제 상태 흐름

```
PENDING ── 결제 승인 ──▶ COMPLETED ── 환불 ──▶ REFUNDED
   │
   └── 결제 실패 ──▶ FAILED
```

---

## 6. 실행 방법

### 사전 준비

- Docker Desktop
- JDK 17 이상
- Node.js 18 이상

### 로컬 실행

```bash
# 1. 인프라 시작 (MySQL + Prometheus + Grafana)
docker-compose up -d

# 2. 백엔드 실행
./gradlew bootRun --args='--spring.profiles.active=dev'

# 3. 프론트엔드 실행 (별도 터미널)
cd frontend
npm install
npm run dev
```

### 접속 주소

| 서비스 | URL |
|--------|-----|
| Frontend | http://localhost:3001 |
| Backend API | http://localhost:8080 |
| Swagger UI | http://localhost:8080/swagger-ui/index.html |
| Grafana | http://localhost:3000 |
| Prometheus | http://localhost:9090 |

### 테스트 계정

| 구분 | 이메일 | 비밀번호 |
|------|--------|---------|
| 관리자 | admin@test.com | test1234 |
| 일반 유저 | user1@test.com ~ user10000@test.com | test1234 |

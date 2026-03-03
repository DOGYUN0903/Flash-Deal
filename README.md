# ⚡ Flash Deal

선착순 한정 수량 상품을 구매할 수 있는 이커머스 플랫폼입니다.
회원가입/로그인부터 상품 탐색, 장바구니, 주문, 결제, 리뷰까지 이커머스 핵심 플로우를 구현했습니다.

---

## 📋 목차

- [✨ 프로젝트 소개](#-프로젝트-소개)
- [🛠️ 기술 스택](#️-기술-스택)
- [🏗️ 시스템 아키텍처](#️-시스템-아키텍처)
- [📊 ERD](#-erd)
- [💡 기술적 의사결정](#-기술적-의사결정)

---

## ✨ 프로젝트 소개

Flash Deal은 한정 수량 상품을 선착순으로 구매하는 이커머스 플랫폼입니다.

단순한 기능 구현을 넘어, 서비스가 성장하면서 마주치는 **실제 성능 병목을 직접 재현하고, 여러 기술을 비교·분석한 뒤 트레이드오프를 고려하여 해결책을 선택하는 과정**을 담은 포트폴리오 프로젝트입니다.

|                |                                                           |
|----------------|-----------------------------------------------------------|
| 🗓️ **개발 기간** | 2026.02 ~ 진행 중                                          |
| 👤 **개발 인원** | 1인 (개인 프로젝트)                                          |
| 🌐 **배포 URL** | https://flash-deal-eight.vercel.app                      |
| 📄 **API 명세** | https://flashdeal.서버.한국/swagger-ui/index.html           |

### 🗂️ 구현 도메인

| 도메인 | 주요 기능 |
|--------|----------|
| 🔐 Auth | 회원가입, 로그인, 로그아웃 |
| 👤 Member | 프로필 조회/수정, 비밀번호 변경 |
| 📦 Product | 상품 등록/수정/삭제/검색 (어드민), 상품 조회/검색 (일반) |
| 🛒 Cart | 장바구니 담기/조회/수량 수정/삭제/비우기 |
| 📝 Order | 장바구니 주문, 바로 구매, 주문 조회/취소, 배송 관리 (어드민) |
| 💳 Payment | 결제 처리, TossPayments 승인, 결제 조회, 환불 |
| ⭐ Review | 구매 확인 후 리뷰 작성, 상품별 리뷰 조회 |

---

## 🛠️ 기술 스택

<div align="center">

**Backend**

![Java](https://img.shields.io/badge/Java_17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot_3.5-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![Spring Security](https://img.shields.io/badge/Spring_Security-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white)
![Spring Data JPA](https://img.shields.io/badge/Spring_Data_JPA-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![QueryDSL](https://img.shields.io/badge/QueryDSL-4695EB?style=for-the-badge&logoColor=white)
![Lombok](https://img.shields.io/badge/Lombok-CA0124?style=for-the-badge&logoColor=white)
![Gradle](https://img.shields.io/badge/Gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white)
![Swagger](https://img.shields.io/badge/Swagger-85EA2D?style=for-the-badge&logo=swagger&logoColor=black)
![Micrometer](https://img.shields.io/badge/Micrometer-6DB33F?style=for-the-badge&logoColor=white)
![Prometheus](https://img.shields.io/badge/Prometheus-E6522C?style=for-the-badge&logo=prometheus&logoColor=white)

**Database**

![MySQL](https://img.shields.io/badge/MySQL_8.0-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
![H2](https://img.shields.io/badge/H2-09476B?style=for-the-badge&logoColor=white)

**Infrastructure**

![NCP](https://img.shields.io/badge/NCP_Server-03C75A?style=for-the-badge&logoColor=white)
![NCP](https://img.shields.io/badge/NCP_Object_Storage-03C75A?style=for-the-badge&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![Grafana](https://img.shields.io/badge/Grafana-F46800?style=for-the-badge&logo=grafana&logoColor=white)
![JMeter](https://img.shields.io/badge/Apache_JMeter-D22128?style=for-the-badge&logo=apachejmeter&logoColor=white)
![TossPayments](https://img.shields.io/badge/TossPayments-0064FF?style=for-the-badge&logoColor=white)

**Frontend**

![Next.js](https://img.shields.io/badge/Next.js-000000?style=for-the-badge&logo=nextdotjs&logoColor=white)
![TypeScript](https://img.shields.io/badge/TypeScript-3178C6?style=for-the-badge&logo=typescript&logoColor=white)
![Tailwind CSS](https://img.shields.io/badge/Tailwind_CSS-06B6D4?style=for-the-badge&logo=tailwindcss&logoColor=white)

**Testing**

![JUnit5](https://img.shields.io/badge/JUnit5-25A162?style=for-the-badge&logo=junit5&logoColor=white)
![Mockito](https://img.shields.io/badge/Mockito-78A641?style=for-the-badge&logoColor=white)
![AssertJ](https://img.shields.io/badge/AssertJ-25A162?style=for-the-badge&logoColor=white)

</div>

---

## 🏗️ 시스템 아키텍처

![Flash Deal Architecture](images/flash-deal_MVP_아키텍처.PNG)

### 🔄 시퀀스 다이어그램 (주문 & 결제)

![시퀀스 다이어그램](images/시퀀스_다이어그램.PNG)

### 📁 패키지 구조

```
src/main/java/com/prj/flashdeal/
├── domain/
│   ├── auth/          # 인증 (회원가입, 로그인)
│   ├── member/        # 회원 관리
│   ├── product/       # 상품 관리
│   ├── cart/          # 장바구니
│   ├── order/         # 주문
│   ├── payment/       # 결제
│   ├── review/        # 리뷰
│   └── file/          # 파일 업로드
└── global/
    ├── config/        # Security, QueryDSL, S3 설정
    ├── entity/        # BaseEntity (createdAt, updatedAt)
    ├── exception/     # 공통 예외 처리
    ├── response/      # ApiResponse, PageResponse
    └── security/      # CustomUserDetails, SecurityConfig
```

---

## 📊 ERD

![Flash Deal ERD](images/flash-deal_MVP_ERD.png)

**주요 Enum 값**

| 테이블 | 컬럼 | 값 |
|--------|------|----|
| MEMBER | role | `USER`, `ADMIN` |
| MEMBER | status | `ACTIVE`, `DORMANT`, `WITHDRAWN`, `BANNED` |
| PRODUCT | status | `PREPARING`, `ON_SALE`, `SOLD_OUT` |
| ORDERS | status | `PENDING`, `PAID`, `SHIPPED`, `DELIVERED`, `CANCELED` |
| PAYMENT | status | `PENDING`, `COMPLETED`, `FAILED`, `REFUNDED` |
| PAYMENT | method | `CARD`, `CASH`, `TRANSFER`, `TOSS` |

---

## 💡 기술적 의사결정

### 🔐 세션 기반 인증 채택

**배경:** 인증 방식으로 세션과 JWT 중 선택이 필요했습니다.

| 방식 | 장점 | 단점 |
|------|------|------|
| **세션** | 서버에서 즉시 무효화 가능, 구현 단순 | 서버 간 세션 공유 필요 (스케일아웃 시) |
| **JWT** | 무상태, 스케일아웃 용이 | 토큰 무효화 어려움 (블랙리스트 필요) |

**채택:** 세션 기반 인증

현재 서비스 규모(단일 서버)에서는 세션 방식이 구현 복잡도 대비 효율적이며, 서버 측 즉시 무효화가 가능하다는 보안 이점이 있습니다. 토큰 무효화가 어려운 JWT의 한계를 고려했을 때 이 프로젝트 규모에서는 세션이 더 적합하다고 판단했습니다.

---

### 💰 가격 스냅샷 저장 (OrderItem)

**배경:** 상품 가격은 시간이 지나면 변경될 수 있습니다.

- `OrderItem.price`, `OrderItem.order_price`: 주문 **당시의** 단가 및 금액 저장

주문 이후 상품 가격이 변경되더라도 결제 금액 내역이 보존됩니다.

---

### 📦 ProductStatus와 재고 수량 연동

**배경:** 상품 상태와 재고 수량이 별개로 관리되면 정합성 문제가 발생합니다.

```
재고 == 0  →  PREPARING 또는 SOLD_OUT
재고 >  0  →  ON_SALE
```

`addStock()` / `removeStock()` 호출 시 엔티티 내부에서 상태를 자동으로 전환합니다. 일반 사용자에게는 `ON_SALE` 상품만 노출되며, 어드민은 상태 무관하게 전체 조회가 가능합니다.

---

### 🗑️ Soft Delete 패턴 (Member, Product)

**배경:** 물리적 삭제 시 연관된 주문/리뷰 데이터의 정합성이 깨집니다.

| 엔티티 | 삭제 처리 |
|--------|----------|
| Member | `is_deleted = true`, `status = WITHDRAWN` |
| Product | `is_deleted = true` |

데이터를 보존하면서 조회 시 삭제된 항목을 필터링합니다. 상품이 삭제되어도 OrderItem에 스냅샷이 저장되어 있어 과거 주문 조회가 가능합니다.

---

### 🔄 결제 멱등성 처리

**배경:** 네트워크 오류 등으로 결제 요청이 중복 전송될 수 있습니다.

결제 처리 시 `findByOrderId()`로 기존 결제 여부를 먼저 확인합니다.

```
결제 요청
  ├── 해당 주문의 기존 결제 존재 → 기존 결제 그대로 반환 (중복 결제 방지)
  └── 없음 → 신규 결제 생성 및 저장
```

동일한 주문에 대한 중복 결제를 방지하고, 클라이언트가 재시도해도 동일한 결과를 반환합니다.

---

### 🧩 도메인 객체에 비즈니스 로직 캡슐화

**배경:** 비즈니스 규칙을 서비스 계층에만 두면 엔티티가 단순 데이터 컨테이너로 전락하고, 같은 검증이 여러 서비스에 흩어집니다.

핵심 규칙은 엔티티 메서드로 캡슐화했습니다.

```java
member.validateActive()         // 활성 상태 검증 (ACTIVE가 아니면 예외)
product.validateVisibleToUser() // 사용자 조회 가능 여부 검증 (ON_SALE + 미삭제)
order.cancel()                  // 취소 가능 상태 검증 후 상태 변경
order.ship() / order.deliver()  // 배송 상태 전환 검증 포함
```

상태 변경과 검증 로직이 엔티티 안에 있어 서비스에서 검증을 빠뜨릴 가능성이 줄고, 단위 테스트 작성도 용이합니다.

---

### 🔍 QueryDSL 도입

**배경:** 상품 검색 시 카테고리, 이름, 상태, 가격 범위 등 조건이 동적으로 조합됩니다.

| 방식 | 문제 |
|------|------|
| JPQL 문자열 조합 | 타입 안전하지 않음, 런타임에 오류 발견 |
| Specification | 복잡한 조건에서 가독성 저하 |
| **QueryDSL** | 타입 안전, IDE 자동완성, 컴파일 타임 오류 감지 |

`*RepositoryCustom` 인터페이스 + `*RepositoryCustomImpl` 구현체 패턴으로 Spring Data JPA와 QueryDSL을 함께 사용합니다.


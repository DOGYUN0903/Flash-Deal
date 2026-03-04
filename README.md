<div align="center">

# ⚡ Flash Deal

</div>

![Flash Deal Cover](images/프로젝트표지.png)

선착순 한정 수량 상품을 구매할 수 있는 이커머스 플랫폼입니다.
회원가입/로그인부터 상품 탐색, 장바구니, 주문, 결제, 리뷰까지 이커머스 핵심 플로우를 구현했습니다.

---

## 📋 목차

- [✨ 프로젝트 소개](#-프로젝트-소개)
- [🛠️ 기술 스택](#️-기술-스택)
- [🏗️ 시스템 아키텍처](#️-시스템-아키텍처)
- [📊 ERD](#-erd)
- [💡 기술적 의사결정](#-기술적-의사결정)
- [🏛️ 도메인 설계 원칙](#️-도메인-설계-원칙)
- [🔧 트러블슈팅](#-트러블슈팅)

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

<details>
<summary><strong>🔐 "JWT가 더 좋다고요?" — 즉시 무효화가 필요한 서비스에서의 선택</strong></summary>

<br>

**배경**

인증 방식을 설계할 때 JWT와 세션 중 하나를 선택해야 했습니다. 많은 프로젝트에서 JWT가 "무상태, 스케일아웃에 유리"하다는 이유로 기본처럼 선택되지만, 먼저 이 서비스의 특성을 고민했습니다.

**고민**

| 방식 | 장점 | 단점 |
|------|------|------|
| **세션** | 서버에서 즉시 무효화 가능, 구현 단순 | 스케일아웃 시 서버 간 세션 공유 필요 |
| **JWT** | 무상태, 스케일아웃 용이 | 만료 전까지 서버가 토큰을 강제 무효화 불가 |

JWT의 근본적인 문제는 **"발급된 토큰은 만료 전까지 서버가 막을 수 없다"** 는 점입니다.
블랙리스트로 해결할 수 있지만, 결국 Redis 등 외부 저장소에 상태를 저장하는 것으로 **"무상태"라는 JWT의 핵심 장점이 퇴색**됩니다.

Flash Deal은 선착순 한정 수량 구매 플랫폼으로, 어뷰징 감지 시 **즉각적인 계정 차단**이 중요합니다. 이 요구사항 앞에서 JWT는 구조적으로 취약합니다.

**결론 — 세션 채택**

- 현재 단일 서버 환경에서 세션은 구현 복잡도가 낮고, 서버 측 즉시 무효화가 가능합니다
- JWT + 블랙리스트 조합은 "무상태"를 포기하면서도 세션보다 복잡합니다
- **이 서비스 규모에서 JWT의 장점(스케일아웃)은 아직 필요 없고, 단점(무효화 불가)은 치명적입니다**

**개선 계획 (V2)**

스케일아웃(3대 서버) 시 세션 불일치 문제 발생 → Spring Session + Redis로 세션 저장소를 외부화하여 해결 예정

</details>

---

<details>
<summary><strong>🔍 "런타임이 아닌 컴파일 타임에 잡는다" — QueryDSL 타입 안전 동적 쿼리</strong></summary>

<br>

**배경**

상품 검색은 이름, 상태, 가격 범위 등 조건이 동적으로 조합됩니다. 이를 구현하는 방법은 여러 가지가 있었습니다.

**고민**

| 방식 | 문제 |
|------|------|
| JPQL 문자열 조합 | 타입 안전하지 않음, 오타가 런타임에야 발견됨 |
| Specification | 코드 가독성 저하, 복잡한 조인 조건에서 한계 |
| **QueryDSL** | 타입 안전, IDE 자동완성, 컴파일 타임 오류 감지 |

**결론 — QueryDSL 채택**

```java
// 조건이 null이면 자동으로 무시되는 타입 안전한 동적 쿼리
BooleanBuilder builder = new BooleanBuilder();
if (name != null)     builder.and(product.name.containsIgnoreCase(name));
if (minPrice != null) builder.and(product.price.goe(minPrice));
if (maxPrice != null) builder.and(product.price.loe(maxPrice));
```

`*RepositoryCustom` 인터페이스 + `*RepositoryCustomImpl` 구현체 패턴으로 Spring Data JPA와 QueryDSL을 함께 사용합니다.
**컬럼명 오타나 타입 불일치는 컴파일 시점에 즉시 감지**됩니다.

</details>

---

## 🏛️ 도메인 설계 원칙

<details>
<summary><strong>🧩 "Service가 아닌 Entity가 스스로 검증한다" — Rich Domain Model 채택</strong></summary>

<br>

**배경**

비즈니스 규칙을 서비스 계층에만 두면 엔티티가 단순 데이터 컨테이너(Anemic Domain Model)로 전락하고, 같은 검증 로직이 여러 서비스에 흩어져 한 곳에서라도 빠뜨리면 바로 버그가 됩니다.

**고민**

| 방식 | 문제 |
|------|------|
| Anemic Domain Model | 검증 로직이 서비스마다 중복, 누락 시 버그 |
| **Rich Domain Model** | 엔티티가 자신의 상태를 스스로 지킴 |

**결론 — 핵심 규칙을 엔티티 메서드로 캡슐화**

```java
member.validateActive()         // ACTIVE가 아니면 즉시 예외
product.validateVisibleToUser() // ON_SALE + 미삭제가 아니면 즉시 예외
order.cancel()                  // 취소 가능 상태 검증 후 CANCELED 전환
order.ship() / order.deliver()  // 배송 상태 전환 시 순서 검증 포함
```

상태 변경과 검증이 엔티티 안에 있어 **서비스에서 검증을 빠뜨릴 가능성 자체가 사라지고**, 엔티티 단위 테스트만으로 핵심 비즈니스 규칙을 검증할 수 있습니다.

</details>

---

<details>
<summary><strong>🗑️ "지워도 기록은 남긴다" — Soft Delete로 연관 데이터 정합성 보장</strong></summary>

<br>

**배경**

회원 탈퇴나 상품 삭제 시 DB에서 물리적으로 삭제하면, 해당 데이터를 참조하는 주문·리뷰 레코드의 FK가 깨져 데이터 정합성이 무너집니다.

**고민**

| 방식 | 문제 |
|------|------|
| Hard Delete | `order_item.product_id` FK 위반, 탈퇴 회원의 주문·리뷰 조회 불가 |
| **Soft Delete** | `is_deleted = true` 플래그로 논리 삭제, 데이터는 보존 |

**결론 — Soft Delete 채택**

| 엔티티 | 삭제 처리 |
|--------|----------|
| Member | `is_deleted = true`, `status = WITHDRAWN`, `deleted_at` 기록 |
| Product | `is_deleted = true`, `deleted_at` 기록 |

- 탈퇴한 회원의 과거 주문·리뷰 데이터가 보존되어 운영 이슈 발생 시 추적 가능
- 삭제된 상품이 있어도 `OrderItem`의 가격 스냅샷으로 과거 주문 조회 정상 동작
- 조회 시 `is_deleted = false` 조건으로 삭제된 항목을 자동 필터링

</details>

---

## 🔧 트러블슈팅

<details>
<summary><strong>🔴 "재고는 50개인데 주문이 100건?" — 동시 주문 시 과매도(Overselling) 해결</strong></summary>

<br>

**🚨 문제 발견**

JMeter로 재고 50개짜리 상품에 100명이 동시에 `POST /api/orders/direct`를 호출하는 부하테스트를 진행했습니다.
정상이라면 50건만 성공해야 하지만 **68건이 성공**했고, Grafana의 `product_stock_remaining` 게이지는 0이 아닌 **7에서 멈추는 현상**을 확인했습니다.

| 구분 | 기대값 | 실제값 |
|------|--------|--------|
| 주문 성공 건수 | 최대 50건 | **68건** |
| 최종 재고 | 0개 | **7개** |
| 과매도 수량 | 0건 | **25건** (68 - 43) |

![Grafana 재고 미감소](images/troubleshooting/grafana-result.png)
![JMeter 68건 성공](images/troubleshooting/jmeter-result.png)

추가로 100건 중 **32건이 500 에러**로 실패했습니다. 서버 로그를 확인하니 아래 예외가 반복 발생하고 있었습니다.

```
Caused by: org.hibernate.exception.LockAcquisitionException: could not execute statement
  [Deadlock found when trying to get lock; try restarting transaction]
  [update products set category=?,description=?,image_url=?,is_deleted=?,name=?,
   price=?,status=?,stock_quantity=?,updated_at=? where product_id=?]
```

![데드락 서버 로그](images/troubleshooting/dead-lock.png)

이는 과매도와 함께 **락 미적용이 낳은 또 다른 증상**입니다.

**🔍 원인 분석 — Lost Update + MySQL 데드락**

```java
// 락 없는 상태 — 일반 SELECT 후 엔티티에서 재고 감소
Product product = productService.findCartableProduct(productId); // 락 없는 SELECT
product.decreaseStock(quantity); // 메모리 값 기준으로 차감 후 UPDATE
```

```
초기 재고: 50개, 동시 요청: 100건

Thread  1: SELECT stock = 50 ┐
Thread  2: SELECT stock = 50 │ 모든 스레드가 동시에
Thread  3: SELECT stock = 50 │ 같은 값(50)을 읽음
   ...                        │
Thread 68: SELECT stock = 50 ┘

Thread  1: UPDATE stock = 49  ┐
Thread  2: UPDATE stock = 49  │ 각자 50-1=49를 계산해
Thread  3: UPDATE stock = 49  │ 서로의 변경을 덮어씀 (Lost Update)
   ...                        │
Thread 68: UPDATE stock = 49  ┘

결과: 주문 68건 성공, 재고 7개 → 25건 과매도
```

`decreaseStock()`에 `stock < quantity` 가드가 있어 음수는 발생하지 않습니다.
진짜 문제는 **여러 트랜잭션이 서로의 업데이트를 덮어써 재고가 제대로 줄지 않는 것**입니다.

**🔍 원인 분석 — MySQL 데드락 (500 에러)**

`order_items`가 `products`에 FK를 가지므로, INSERT 시 MySQL이 부모 행(`products`)에 **공유 락(S-Lock)** 을 획득합니다.
동시에 `UPDATE products SET stock_quantity = ?`는 동일 행에 **배타 락(X-Lock)** 을 요구합니다.
두 트랜잭션이 서로의 S-Lock을 기다리며 X-Lock을 획득하지 못하는 순환 대기가 발생합니다.

```
TX-A: order_items INSERT → products 행에 S-Lock 획득
TX-B: order_items INSERT → products 행에 S-Lock 획득

TX-A: products UPDATE → X-Lock 필요, TX-B의 S-Lock 대기 중
TX-B: products UPDATE → X-Lock 필요, TX-A의 S-Lock 대기 중

→ Deadlock! MySQL이 한 쪽을 Victim으로 롤백
```

`LockAcquisitionException`은 `CustomException`을 상속하지 않아 `GlobalExceptionHandler`의 `@ExceptionHandler(Exception.class)`에 걸려 **500**으로 응답됩니다.
비관적 락(`SELECT FOR UPDATE`)을 적용하면 한 트랜잭션이 X-Lock을 선점하여 다른 트랜잭션이 대기하므로, 이 순환 대기 자체가 발생하지 않습니다.

**⚖️ 해결 방법 비교**

| 방법 | 동작 방식 | 장점 | 단점 |
|------|----------|------|------|
| **낙관적 락** (`@Version`) | 커밋 시 버전 충돌 감지 → 예외 | DB 락 없음, 충돌 없을 때 성능 우수 | 고경합 시 `OptimisticLockException` 폭증 → 재시도 폭풍, 실패율 급등 |
| **비관적 락** (`SELECT FOR UPDATE`) | 조회 시점에 행 락 점유 → 직렬화 | 정합성 확실, 구현 단순 | 처리량 한계, 락 대기로 응답 지연 |
| **Redis 분산 락** (Redisson) | Redis 기반 외부 락 | DB 독립적, 분산 환경에 유리 | 단일 서버에서 공유 메모리 사용 가능한데 Redis 인프라까지 추가하는 건 오버엔지니어링 → 배제 |

**🧪 낙관적 락 적용 및 테스트**

`@Version` 필드만 추가하고 동일한 동시 주문 테스트(재고 50개, 100명 동시 요청)를 실행했습니다.

```java
// Product 엔티티 — 낙관적 락
@Version
private Long version;

// 커밋 시 Hibernate가 자동으로 아래 쿼리 실행
// UPDATE products SET stock_quantity=?, version=? WHERE product_id=? AND version=?
// → 다른 트랜잭션이 먼저 커밋했다면 version 불일치 → ObjectOptimisticLockingFailureException
```

서버 로그에는 아래 예외가 반복 발생했습니다.

```
Caused by: org.hibernate.exception.LockAcquisitionException: could not execute statement
  [Deadlock found when trying to get lock; try restarting transaction]
  [update products set ...,version=? where product_id=? and version=?]
```

![낙관적 락 Grafana](images/troubleshooting/낙관락-grafana.png)
![낙관적 락 JMeter](images/troubleshooting/낙관락-jmeter.png)

낙관적 락은 `UPDATE ... WHERE version=?` 조건으로 버전 충돌을 감지하지만, **데드락 자체를 막지는 못합니다.**
`order_items INSERT`의 FK S-Lock과 `products UPDATE`의 X-Lock이 여전히 순환 대기를 일으킵니다.
충돌이 발생할 때마다 예외가 throw되고, 재시도 로직 없이는 대부분 요청이 실패합니다.
재시도를 추가해도 고경합 환경에서는 재시도 요청이 연쇄 폭증해 오히려 서버 부하가 커집니다.
**동시 주문이 잦은 선착순 환경에서는 낙관적 락이 부적합**하다고 판단했습니다.

**✅ 채택 — 비관적 락 (Pessimistic Lock)**

낙관적 락의 실패를 직접 확인한 뒤, `SELECT ... FOR UPDATE`로 전환했습니다.

```java
// ProductRepository — SELECT FOR UPDATE 쿼리 추가
@Lock(LockModeType.PESSIMISTIC_WRITE)
@Query("SELECT p FROM Product p WHERE p.id = :productId")
Optional<Product> findByIdWithLock(@Param("productId") Long productId);

// ProductService.findCartableProduct() — SELECT FOR UPDATE로 변경
// 엔티티의 첫 번째 접근을 락 쿼리로 만들어 Hibernate 1차 캐시 stale 문제 방지
@Transactional
public Product findCartableProduct(Long productId) {
    Product product = productRepository.findByIdWithLock(productId)
            .orElseThrow(...);  // SELECT ... FOR UPDATE → 다른 트랜잭션 대기
    ...
    return product;
}

// OrderService.createDirectOrder() — 락이 걸린 엔티티에서 직접 재고 감소
Product product = productService.findCartableProduct(request.getProductId());
product.decreaseStock(request.getQuantity()); // 엔티티가 자신의 재고를 직접 관리
```

한 트랜잭션이 락을 점유하는 동안 다른 트랜잭션은 대기하므로, 항상 최신 재고를 기준으로 차감됩니다.
재고 50개에 100건 요청 시 정확히 50건만 성공하고, Grafana 게이지가 50 → 0으로 정확히 감소합니다.

![비관적 락 Grafana](images/troubleshooting/비관락-grafana.png)
![비관적 락 JMeter](images/troubleshooting/비관락-jmeter.png)

</details>


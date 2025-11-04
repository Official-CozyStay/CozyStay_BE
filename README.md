# 🌿 CozyStay (Backend)

> **숙소 검색/예약/리뷰/호스트 관리**
> Java & Spring 기반

## 주요 기능

| 도메인        | 기능                                                                 |
|--------------|----------------------------------------------------------------------|
| 인증/권한     | 회원가입, 로그인(JWT), 소셜로그인(선택), 역할(Role: USER/HOST/ADMIN) |
| 숙소(Listing) | 숙소 등록/수정/조회, 사진 업로드(사전서명 URL), 편의시설/정책 관리   |
| 검색/필터     | 지역/날짜/인원/가격/유형 필터, 정렬(평점/가격/리뷰수 등)             |
| 예약(Booking) | 가용성 체크, 예약 생성/취소/환불 규칙, 캘린더 블로킹                 |
| 리뷰(Review)  | 예약 완료 사용자만 작성 가능, 평점/코멘트, 신고/숨김                 |
| 결제(선택)    | 결제 요청/콜백(모의/PG 연동), 영수증(모의)                            |
| 알림(선택)    | 이메일/푸시(모의), 예약 상태 변경 및 호스트 알림                      |
| 운영 도구     | 어드민 대시보드(기본 엔드포인트: 통계/신고처리), 헬스체크             |


## 기술 스택

| 항목             | 내용                                                                                  |
|------------------|---------------------------------------------------------------------------------------|
| Language         | **Java 17/21** (팀 기준에 맞춰 선택: 17 안정/21 최신 기능)                           |
| Framework        | **Spring Boot 3.x** (Jakarta EE 10), Spring MVC, Spring Validation                    |
| Security         | **Spring Security 6**, JWT(Access/Refresh), OAuth2 Client(선택)                       |
| Data             | **JPA(Hibernate)** + QueryDSL(or MyBatis 택1), **Flyway**(DB 마이그레이션)            |
| Database         | **MySQL 8.x** (로컬은 H2 인메모리 옵션 지원), Redis(세션/락/레이트리밋 선택)         |
| Build            | **Gradle**                                                                             |
| Docs             | **springdoc-openapi** (Swagger UI)                                                     |
| Test             | JUnit5, Mockito, Spring Boot Test, Testcontainers(선택)                                |
| Infra (선택)     | Docker/Docker Compose, GitHub Actions, AWS RDS/EC2/S3(사전서명 업로드)                 |

> **참고**: JPA가 포함되어 있으면 부트가 **DataSource 자동 설정**을 시도해 DB가 필요합니다. H2 인메모리 또는 DataSource 자동설정 제외로 “DB 없이”도 실행할 수 있도록 옵션을 아래에 제공합니다.

## 프로젝트 구조

```bash
cozystay-backend/
├─ build.gradle
├─ settings.gradle
├─ gradlew / gradlew.bat
├─ Dockerfile
├─ docker-compose.yml              # (선택) MySQL/Redis 로컬 실행
├─ src
│  ├─ main
│  │  ├─ java/com/cozystay
│  │  │  ├─ CozyStayApplication.java
│  │  │  ├─ common/               # 공통(예외, 응답 래퍼, AOP, 유틸)
│  │  │  ├─ config/               # Security/JPA/Swagger/Redis/Storage
│  │  │  ├─ auth/                 # 인증·인가(JWT, OAuth2)
│  │  │  ├─ user/                 # 사용자/호스트/관리자
│  │  │  ├─ listing/              # 숙소, 사진, 편의시설, 정책
│  │  │  ├─ search/               # 검색/필터/정렬
│  │  │  ├─ booking/              # 예약/가용성/취소
│  │  │  ├─ review/               # 리뷰/평점/신고
│  │  │  └─ admin/                # 운영/통계/헬스체크
│  │  └─ resources
│  │     ├─ application.yml
│  │     ├─ application-local.yml # 로컬(H2 또는 MySQL)
│  │     ├─ application-dev.yml   # 개발(공유 DB/Redis)
│  │     ├─ application-prod.yml  # 운영
│  │     └─ db/migration          # Flyway 마이그레이션 스크립트 (V1__init.sql 등)
│  └─ test/java/com/cozystay      # 단위/통합 테스트
```

## 환경 변수

`.env` (로컬 예시 – Compose나 IDE Run Config로 주입)

```env
SPRING_PROFILES_ACTIVE=local
DB_HOST=localhost
DB_PORT=3306
DB_NAME=cozystay
DB_USERNAME=root
DB_PASSWORD=secret
JWT_SECRET=change-me
S3_BUCKET=cozystay-bucket
S3_REGION=ap-northeast-2
```

## 설정 예시

### 1) 기본 `application.yml`

```yaml
spring:
  application:
    name: cozystay
  profiles:
    default: local

server:
  port: 8080

springdoc:
  api-docs:
    enabled: true
  swagger-ui:
    path: /swagger-ui.html
```

### 2) 로컬: **MySQL 사용**

```yaml
# application-local.yml
spring:
  datasource:
    url: jdbc:mysql://${DB_HOST:localhost}:${DB_PORT:3306}/${DB_NAME:cozystay}?useSSL=false&serverTimezone=Asia/Seoul&characterEncoding=UTF-8
    username: ${DB_USERNAME:root}
    password: ${DB_PASSWORD:secret}
  jpa:
    hibernate:
      ddl-auto: validate
    properties:
      hibernate:
        format_sql: true
        show_sql: true

jwt:
  secret: ${JWT_SECRET:change-me}
```

### 3) 로컬: **DB 없이 빠르게 실행(택1)**

- **옵션 A – H2 인메모리 사용**

```yaml
# application-local.yml
spring:
  datasource:
    url: jdbc:h2:mem:cozystay;MODE=MySQL;DB_CLOSE_DELAY=-1
    driver-class-name: org.h2.Driver
    username: sa
    password:
  jpa:
    hibernate:
      ddl-auto: create
    properties:
      hibernate:
        format_sql: true
  h2:
    console:
      enabled: true
```

- **옵션 B – DataSource/JPA 자동설정 제외(완전 DB 미사용)**  
  *(JPA 동작 안 함. 컨트롤러/헬스체크만 필요할 때)*

```yaml
# application-local.yml
spring:
  autoconfigure:
    exclude:
      - org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
      - org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration
```

> 팀 개발 초반에는 **옵션 A(H2)**로 API 껍데기부터 맞추고,  
> 도메인/리포지토리 안정화되면 **MySQL 전환**을 추천합니다.

## 실행 방법

```bash
# 0) (선택) 로컬 DB/Redis
docker compose up -d   # docker-compose.yml에 MySQL/Redis 정의되어 있는 경우

# 1) 빌드
./gradlew clean build

# 2) 실행 (로컬 프로필)
java -jar build/libs/cozystay-0.0.1-SNAPSHOT.jar --spring.profiles.active=local
```

> IntelliJ/VSCode에서도 Run/Debug 구성에 `SPRING_PROFILES_ACTIVE=local` 지정

## API 문서 (Swagger)

- 실행 후: `http://localhost:8080/swagger-ui.html`  
- OpenAPI 스키마: `/v3/api-docs`

## 주요 엔드포인트 (요약)

| 메서드 | 경로                                 | 설명                               | 인증 |
|--------|--------------------------------------|------------------------------------|------|
| POST   | /api/auth/signup                     | 회원가입                            | -    |
| POST   | /api/auth/login                      | 로그인(JWT 발급)                    | -    |
| GET    | /api/listings                        | 숙소 목록/검색                      | -    |
| POST   | /api/listings                        | 숙소 등록(호스트)                   | HOST |
| GET    | /api/listings/{id}                   | 숙소 상세                            | -    |
| POST   | /api/bookings                        | 예약 생성                            | USER |
| GET    | /api/bookings/my                     | 내 예약 목록                         | USER |
| POST   | /api/reviews                         | 리뷰 작성(예약 완료 사용자만)        | USER |
| GET    | /actuator/health                     | 헬스체크                             | -    |

> 실제 스펙은 Swagger에서 최신 상태를 확인하세요.

## 데이터 모델(개요)

- **User**(role: USER/HOST/ADMIN), **Profile**  
- **Listing**(Amenity, Photo, Policy)  
- **Availability/Calendar** (가용성 관리)  
- **Booking**(상태: REQUESTED/CONFIRMED/CANCELLED/COMPLETED)  
- **Review**(평점, 코멘트, 신고 플래그)  
- **Payment**(선택: 모의/PG)

> 스키마 변경은 **Flyway** `db/migration`에 버전업 스크립트를 추가합니다(`V2__add_review.sql` 등).

## 개발 규칙

- **계층 구조**: `controller → service → repository → domain(entity)`  
- **DTO 분리**: Request/Response DTO 명확 분리, 엔티티 직접 노출 금지  
- **예외 처리**: 글로벌 핸들러(`@ControllerAdvice`)로 표준 에러 응답  
- **검증**: Bean Validation(`@Valid`) + 커스텀 Validator  
- **트랜잭션**: 서비스 계층 단위(`@Transactional`)  
- **로그/감사**: 요청/응답 요약 로그, 주요 도메인 이벤트 감사 로그

## 테스트 전략

- **단위**: 순수 서비스/도메인(JUnit5, Mockito)  
- **통합**: `@SpringBootTest` + Testcontainers(MySQL/Redis)  
- **문서화(선택)**: REST Docs 또는 springdoc 샘플 스냅샷 테스트

## 배포(예시)

- **컨테이너**: `Dockerfile` 멀티스테이지 빌드  
- **CI/CD**: GitHub Actions(빌드/테스트/도커 푸시)  
- **운영**: AWS RDS(MySQL), EC2 또는 ECS, S3(사진 사전서명 업로드)

## 라이선스

본 프로젝트 소스 코드는 **청춘여행 팀 내부 사용**을 목적으로 하며,  
허가되지 않은 무단 복제 및 배포를 금합니다.

---

### 빠른 체크리스트 (문제 발생 시)

- `spring-boot-starter-data-jpa`가 있다면 → **DB(H2/MySQL) 설정 필요**  
- DB 없이 띄우려면 → **H2** 사용 또는 **DataSource/JPA 자동설정 제외**  
- Swagger 404 → springdoc 의존성과 `swagger-ui` 경로 확인  
- `Failed to configure a DataSource` → datasource URL/드라이버/프로필 확인

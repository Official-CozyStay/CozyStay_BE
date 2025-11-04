# 🏡 CozyStay (Backend)

> **숙소 검색/예약/리뷰/호스트 관리**
> Java & Spring 기반

## 주요 기능

| 도메인         | 기능                                                                 |
|-------------|----------------------------------------------------------------------|
| 인증/보안       | 회원가입, 로그인(JWT), 소셜로그인(선택), 역할(Role: USER/HOST/ADMIN) |
| 숙소(Listing) | 숙소 등록/수정/조회, 사진 업로드(사전서명 URL), 편의시설/정책 관리   |
| 검색/필터       | 지역/날짜/인원/가격/유형 필터, 정렬(평점/가격/리뷰수 등)             |
| 예약(Booking) | 가용성 체크, 예약 생성/취소/환불 규칙, 캘린더 블로킹                 |
| 리뷰(Review)  | 예약 완료 사용자만 작성 가능, 평점/코멘트, 신고/숨김                 |
| 결제          | 결제 요청/콜백(모의/PG 연동), 영수증(모의)                            |


## 기술 스택

| 항목             | 내용                                                            |
|------------------|---------------------------------------------------------------|
| Language         | **Java 21**                                                   |
| Framework        | **Spring Boot 3.x** (Jakarta EE 10), Spring MVC, Spring Validation |
| Security         | **Spring Security 6**, JWT(Access/Refresh), OAuth2 Client  |
| Data             | **JPA(Hibernate)**                                            |
| Database         | **MySQL 8.x**, Redis(세션/락/레이트리밋 선택)                      |
| Build            | **Gradle**                                                    |
| Docs             | **springdoc-openapi** (Swagger UI)                            |
| Test             | JUnit5, Mockito, Spring Boot Test            |
| Infra  | Docker/Docker Compose, GitHub Actions, AWS RDS/EC2/S3  |


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
│  │  │  ├─ properties/              # 숙소, 사진, 편의시설, 정책
│  │  │  ├─ search/               # 검색/필터/정렬
│  │  │  ├─ booking/              # 예약/취소
│  │  │  ├─ review/               # 리뷰/평점/신고 
│  │  └─ resources
│  │     ├─ application.yml
│  │     ├─ application-local.yml # 로컬(H2 또는 MySQL)
│  │     ├─ application-prod.yml  # 운영
│  └─ test/java/com/cozystay      # 단위/통합 테스트
```

## 환경 변수

`.env` 

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

### 2) 운영: EC2용

```yaml
# application-local.yml
spring:
  profiles:
    include: oauth
  datasource:
    url: ${SPRING_DATASOURCE_URL}
    username: ${SPRING_DATASOURCE_USERNAME}
    password: ${SPRING_DATASOURCE_PASSWORD}
    driver-class-name: com.mysql.cj.jdbc.Driver
  jpa:
    hibernate:
      ddl-auto: update
    properties:
      hibernate:
        format_sql: true
        jdbc.time_zone: Asia/Seoul

  security:
    oauth2:
      client:
        registration:
          kakao:
            client-id: ${KAKAO_CLIENT_ID}
            client-secret: ${KAKAO_CLIENT_SECRET}
            redirect-uri: ${KAKAO_REDIRECT_URI}
            client-authentication-method: client_secret_post
            authorization-grant-type: authorization_code
            scope: profile_nickname, account_email
            client-name: Kakao
        provider:
          kakao:
            authorization-uri: https://kauth.kakao.com/oauth/authorize
            token-uri: https://kauth.kakao.com/oauth/token
            user-info-uri: https://kapi.kakao.com/v2/user/me
            user-name-attribute: id
  redirect:
    frontend-url: https://cozystay.site.com
logging:
  level:
    org.hibernate.SQL: debug

jwt:
  secret: ${JWT_SECRET}
  expiration: ${JWT_EXPIRATION:3600}

```

## 실행 방법

```bash
# 0) (선택) 로컬 DB/Redis
docker compose up -d   # docker-compose.yml에 MySQL/Redis 정의되어 있는 경우

# 1) 빌드
./gradlew clean build

# 2) 실행 (로컬 프로필)
java -jar build/libs/cozystay-0.0.1-SNAPSHOT.jar --spring.profiles.active=local
```


## API 문서 (Swagger)

- 실행 후: `http://localhost:8080/swagger-ui.html`  
- OpenAPI 스키마: `/v3/api-docs`
> 실제 스펙은 Swagger에서 최신 상태를 확인하세요.

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

## 배포

- **컨테이너**: `Dockerfile` 멀티스테이지 빌드  
- **CI/CD**: GitHub Actions(빌드/테스트/도커 푸시)  
- **운영**: AWS RDS(MySQL), EC2 또는 ECS, S3

## 라이선스

본 프로젝트 소스 코드는 **CozyStay 팀 내부 사용**을 목적으로 하며,  
허가되지 않은 무단 복제 및 배포를 금합니다.

---

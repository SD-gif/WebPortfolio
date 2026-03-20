# WebPortfolio Backend

[![CI/CD](https://github.com/SD-gif/WebPortfolio/actions/workflows/cicd.yml/badge.svg)](https://github.com/SD-gif/WebPortfolio/actions/workflows/cicd.yml)
[![codecov](https://codecov.io/gh/SD-gif/WebPortfolio/graph/badge.svg?token=44TRVEHLXG)](https://codecov.io/gh/SD-gif/WebPortfolio)

웹 포트폴리오 서비스의 백엔드 API 서버입니다.
Spring Boot 기반으로 구현되었으며, JWT 인증·Redis Rate Limiting·Flyway 마이그레이션·Docker 배포까지 포함합니다.

---

## 기술 스택

| 분류 | 기술 |
|------|------|
| Language | Java 17 |
| Framework | Spring Boot 3.4.3 |
| Security | Spring Security 6, JWT (jjwt 0.12.6) |
| Database | PostgreSQL, Redis |
| ORM | Spring Data JPA (Hibernate) |
| Migration | Flyway |
| Mail | Naver SMTP |
| Docs | Swagger / SpringDoc OpenAPI 2.8.6 |
| Build | Gradle |
| Deploy | Docker, AWS EC2 |
| CI/CD | GitHub Actions |
| Coverage | JaCoCo, Codecov |

---

## 주요 기능

### 공개 API
| 기능 | 엔드포인트 |
|------|-----------|
| 프로필 조회 | `GET /api/profile` |
| 프로젝트 목록 조회 (페이지네이션) | `GET /api/projects?page=0&size=10` |
| 프로젝트 단건 조회 | `GET /api/projects/{id}` |
| 스킬 목록 조회 | `GET /api/skills` |
| 학력 목록 조회 | `GET /api/educations` |
| 자격증·수상 목록 조회 | `GET /api/certifications` |
| 문의 전송 | `POST /api/contact` |

### 관리자 API (JWT 필요)
| 기능 | 엔드포인트 |
|------|-----------|
| 로그인 | `POST /api/admin/login` |
| 프로필 수정 | `PUT /api/admin/profile` |
| 프로젝트 등록·수정·삭제 | `POST/PUT/DELETE /api/admin/projects/{id}` |
| 스킬 카테고리·스킬 관리 | `POST/PUT/DELETE /api/admin/skills/**` |
| 학력 등록·수정·삭제 | `POST/PUT/DELETE /api/admin/educations/{id}` |
| 자격증 등록·수정·삭제 | `POST/PUT/DELETE /api/admin/certifications/{id}` |
| 문의 목록 조회 | `GET /api/admin/contacts` |
| 문의 읽음 처리 | `PATCH /api/admin/contacts/{id}/read` |

### 부가 기능
- **Rate Limiting** — 문의 전송 IP당 분당 3건 제한 (Redis 기반)
- **이메일 발송** — 문의 수신 시 네이버 SMTP로 자동 발송
- **API 문서** — Swagger UI 제공 (`/swagger-ui/index.html`)

---

## 프로젝트 구조

```
src/main/java/com/seodong/portfolio/
├── common/
│   ├── config/          # Security, Redis, Swagger, CORS 설정
│   ├── security/        # JWT 발급·검증, 인증 필터
│   ├── exception/       # 전역 예외 핸들러, 커스텀 예외
│   └── dto/             # 공통 응답 DTO (PageResponse, SimpleResponse 등)
├── admin/               # 관리자 인증
├── profile/             # 프로필 조회·수정
├── project/             # 프로젝트 CRUD
├── skill/               # 스킬 카테고리·스킬 CRUD
├── education/           # 학력 CRUD
├── certification/       # 자격증·수상 CRUD
└── contact/             # 문의 전송·조회

src/main/resources/
├── application.yml          # 공통 설정
├── application-local.yml    # 로컬 개발용 (git 제외)
├── application-prod.yml     # 운영 (환경변수 참조)
├── db/migration/            # Flyway SQL 마이그레이션
└── static/                  # 프론트엔드 정적 파일
```

---

## 로컬 실행

### 사전 준비

- Java 17
- Docker (PostgreSQL, Redis 실행용)

### 1. 인프라 실행

```bash
docker run -d --name postgres \
  -e POSTGRES_DB=portfolio \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -p 5432:5432 postgres:16-alpine

docker run -d --name redis \
  -p 6379:6379 redis:7-alpine
```

### 2. 환경 설정

`src/main/resources/application-local.yml` 파일을 생성하고 아래 내용을 작성합니다.

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/portfolio
    username: postgres
    password: postgres
    driver-class-name: org.postgresql.Driver
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: true
  data:
    redis:
      host: localhost
      port: 6379
  mail:
    host: smtp.naver.com
    port: 465
    username: {네이버 이메일}
    password: {네이버 앱 비밀번호}
    properties:
      mail.smtp.ssl.enable: true
      mail.smtp.auth: true

jwt:
  secret: local-dev-secret-key-min-32-characters-long!!

cors:
  allowed-origins:
    - http://localhost:3000

rate-limit:
  contact:
    capacity: 3
    refill-minutes: 1
```

### 3. 서버 실행

```bash
./gradlew bootRun
```

서버 기동 후 Flyway가 자동으로 DB 스키마와 초기 데이터를 생성합니다.

- API 서버: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui/index.html`

---

## 테스트

```bash
# 전체 테스트 실행
./gradlew test

# 테스트 + 커버리지 리포트 생성
./gradlew test jacocoTestReport
```

커버리지 리포트: `build/reports/jacoco/test/html/index.html`

테스트는 H2 인메모리 DB로 실행되어 PostgreSQL·Redis 없이도 동작합니다.

---

## API 응답 형식

### 에러 응답

```json
{
  "status": 404,
  "error": "Not Found",
  "message": "해당 프로젝트를 찾을 수 없습니다.",
  "path": "/api/projects/99",
  "errors": null
}
```

### 유효성 오류 (400)

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "입력값이 올바르지 않습니다.",
  "errors": {
    "email": "올바른 이메일 형식이 아닙니다.",
    "message": "내용을 입력해주세요."
  }
}
```

---

## 배포 구조

```
GitHub Push (main)
    │
    ├── [1] Test & Coverage
    │       └── JaCoCo 리포트 → Codecov 업로드
    │
    ├── [2] Docker Build & Push
    │       └── Docker Hub ({username}/web_potfolio:latest)
    │
    └── [3] EC2 Deploy
            └── SSH → docker-compose pull & up
```

### 필요한 GitHub Secrets

| Secret | 설명 |
|--------|------|
| `DOCKERHUB_USERNAME` | Docker Hub 계정명 |
| `DOCKERHUB_TOKEN` | Docker Hub Access Token |
| `EC2_HOST` | EC2 퍼블릭 IP |
| `EC2_USER` | EC2 접속 유저 (ubuntu 등) |
| `EC2_KEY` | EC2 SSH Private Key |
| `CODECOV_TOKEN` | Codecov 업로드 토큰 |

---

## DB 스키마

Flyway로 관리됩니다. 마이그레이션 파일 위치: `src/main/resources/db/migration/`

| 버전 | 내용 |
|------|------|
| V1 | 전체 스키마 초기 생성 |
| V2 | 초기 데이터 삽입 |
| V3 | Admin 비밀번호 수정 |
| V4 | 학력·자격증 테이블 추가 |

---

## 환경변수 (운영)

| 변수 | 설명 |
|------|------|
| `DB_HOST` | PostgreSQL 호스트 |
| `DB_NAME` | DB 이름 |
| `DB_USERNAME` | DB 유저명 |
| `DB_PASSWORD` | DB 비밀번호 |
| `REDIS_HOST` | Redis 호스트 |
| `REDIS_PASSWORD` | Redis 비밀번호 |
| `JWT_SECRET` | JWT 서명 키 (32자 이상) |
| `MAIL_USERNAME` | SMTP 이메일 계정 |
| `MAIL_PASSWORD` | SMTP 앱 비밀번호 |
| `FRONTEND_URL` | CORS 허용 프론트엔드 URL |

# HIS-Link Backend

한동대 개발자를 위한 올인원 커뮤니티 플랫폼 **HIS-Link**의 Spring Boot 백엔드 API 서버입니다.

## Live Deployment

| 항목 | URL |
|------|-----|
| **API Base** | https://purpleworld.cloud/api |
| **Swagger UI** | https://purpleworld.cloud/swagger-ui/index.html |
| **Health Check** | https://purpleworld.cloud/actuator/health |

## Tech Stack

- Java 11
- Spring Boot 2.7
- Spring Security + OAuth2 Client (Google)
- JWT (access / refresh)
- Spring Data JPA + MySQL
- SpringDoc OpenAPI (Swagger)

## Features (AR1–AR5)

| 모듈 | 설명 |
|------|------|
| **AR1** Auth | Google OAuth, JWT 발급·갱신, 한동 이메일 도메인 검증 |
| **AR2** Main | `GET /api/main/dashboard` — 메인 카드 집계 |
| **AR3** Community | 게시글·댓글·좋아요 CRUD |
| **AR4** Lab | 프로젝트 등록·피드백·정렬 (최신/인기/피드백) |
| **AR5** Recruitment | 모집글·필터·지원(댓글) |

## Prerequisites

- JDK 11+
- MySQL 8
- Google OAuth 2.0 클라이언트 ID / Secret

## Quick Start (Local)

```bash
# 1. 환경 변수 설정
cp .env.example .env
# .env 값을 편집 (DB, JWT_SECRET, GOOGLE_* 등)

# 2. MySQL 데이터베이스 생성
# CREATE DATABASE hislink CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

# 3. 서버 실행
./gradlew bootRun
# Windows: gradlew.bat bootRun
```

기본 프로필은 `local` (`application.yml` → `spring.profiles.active: local`).

- API: http://localhost:8080/api
- Swagger: http://localhost:8080/swagger-ui/index.html

## Environment Variables

프로젝트 루트의 `.env` 파일을 사용합니다. (`application-local.yml`이 `optional:file:.env`로 로드)

`.env.example`을 복사해 값을 채웁니다.

| 변수 | 설명 | 예시 |
|------|------|------|
| `DB_URL` | MySQL JDBC URL | `jdbc:mysql://localhost:3306/hislink?...` |
| `DB_USERNAME` | DB 사용자 | `root` |
| `DB_PASSWORD` | DB 비밀번호 | |
| `JWT_SECRET` | HS256 시크릿 (32자 이상) | |
| `JWT_ACCESS_EXPIRE_MS` | access token 만료 (ms) | `3600000` |
| `JWT_REFRESH_EXPIRE_MS` | refresh token 만료 (ms) | `1209600000` |
| `GOOGLE_CLIENT_ID` | Google OAuth Client ID | |
| `GOOGLE_CLIENT_SECRET` | Google OAuth Client Secret | |
| `ALLOWED_EMAIL_DOMAINS` | 허용 이메일 도메인 | `handong.ac.kr,handong.edu` |
| `CORS_ALLOWED_ORIGINS` | CORS 허용 Origin | `http://localhost:3000` |
| `FRONTEND_AUTH_CALLBACK_URL` | OAuth 후 FE 리다이렉트 | `http://localhost:3000/auth/callback` |
| `SERVER_PORT` | 서버 포트 | `8080` |
| `DEV_AUTH_BYPASS` | 로컬 인증 우회 (개발용) | `true` |
| `DEV_USER_EMAIL` | 우회 시 기본 사용자 이메일 | `dev@handong.ac.kr` |

### 배포 (`deploy` 프로필) 추가 변수

`application-deploy.yml` 기준:

| 변수 | 설명 |
|------|------|
| `PUBLIC_BASE_URL` | 업로드 파일 공개 URL 베이스 (예: `https://purpleworld.cloud`) |
| `UPLOAD_DIR` | 업로드 저장 경로 (기본 `/var/hislink/uploads`) |
| `CORS_ALLOWED_ORIGINS` | `https://purpleworld.cloud` |
| `FRONTEND_AUTH_CALLBACK_URL` | `https://purpleworld.cloud/auth/callback` |
| `DEV_AUTH_BYPASS` | 반드시 `false` |

## Profiles

| Profile | 용도 | 설정 파일 |
|---------|------|-----------|
| `local` | 로컬 개발 (기본) | `application-local.yml` + `.env` |
| `deploy` | 프로덕션 / Docker | `application-deploy.yml` + 환경 변수 |
| `test` | JUnit 테스트 (H2) | `application-test.yml` |

## Build & Test

```bash
# 단위·통합 테스트
./gradlew test

# WAR 빌드
./gradlew bootWar
# 결과: build/libs/*.war
```

## Docker (Optional)

```bash
docker build -t hislink-be .
docker run -p 8080:8080 --env-file .env hislink-be
```

컨테이너는 `deploy` 프로필로 기동됩니다.

## API Overview

자세한 스펙: [docs/design/API.md](docs/design/API.md)

| Base Path | 설명 |
|-----------|------|
| `/api/auth` | 인증 (me, refresh, logout) |
| `/api/community/posts` | 커뮤니티 |
| `/api/lab/projects` | User Testing Lab |
| `/api/recruitment/posts` | 팀 모집 |
| `/api/main/dashboard` | 메인 대시보드 |
| `/api/tech-stacks` | 기술 스택 목록 |

### Google OAuth (브라우저)

```
GET http://localhost:8080/oauth2/authorization/google
```

로그인 성공 시 `FRONTEND_AUTH_CALLBACK_URL`로 JWT 쿼리 파라미터와 함께 리다이렉트됩니다.

## Project Structure

```
src/main/java/com/hislink/
├── config/              # Security, Swagger, CORS
├── common/              # 예외, 페이징, AuthorValidator
└── domain/
    ├── auth/            # OAuth, JWT
    ├── user/
    ├── community/       # AR3
    ├── lab/             # AR4
    ├── recruitment/     # AR5
    ├── main/            # AR2
    └── techstack/

docs/design/             # ERD, API, CONVENTIONS, SWAGGER
```

## Design Docs

| 문서 | 설명 |
|------|------|
| [docs/design/README.md](docs/design/README.md) | 설계 문서 인덱스 |
| [docs/design/API.md](docs/design/API.md) | REST API 스펙 |
| [docs/design/ERD.md](docs/design/ERD.md) | DB ERD |
| [docs/design/CONVENTIONS.md](docs/design/CONVENTIONS.md) | 응답·권한·페이징 규칙 |
| [docs/design/SWAGGER.md](docs/design/SWAGGER.md) | OpenAPI 작성 가이드 |

## Local Dev Tips

- **`DEV_AUTH_BYPASS=true`**: Swagger/API 테스트 시 JWT 없이 `DEV_USER_EMAIL` 사용자로 동작합니다. 배포 환경에서는 사용하지 마세요.
- **Swagger**: http://localhost:8080/swagger-ui/index.html — Bearer 토큰 또는 dev bypass로 인증 API 테스트
- **업로드 파일**: 로컬 기본 경로 `uploads/`, URL `/uploads/**`

## Team

Kim Sangmin · Park Sijin · Jeon Seyeon · Kwak Seowon

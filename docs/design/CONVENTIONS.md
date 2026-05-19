# HIS-Link API 공통 규칙

FE·BE가 맞춰야 할 계약입니다. 2단계(커뮤니티) 구현부터 이 문서를 기준으로 합니다.

---

## 1. 응답 형식

모든 REST API는 JSON 래퍼 `ApiResponse<T>`를 사용합니다.

```json
{
  "success": true,
  "data": { },
  "message": null
}
```

실패 시:

```json
{
  "success": false,
  "data": null,
  "message": "에러 메시지"
}
```

- `success`: 처리 성공 여부
- `data`: 성공 시 payload (없으면 `null`)
- `message`: 실패 메시지 또는 성공 안내 문구

---

## 2. 인증

### 로컬 개발 (auth-bypass)

`local` 프로필 + `DEV_AUTH_BYPASS=true`(기본값)이면 JWT 없이 API 호출 가능합니다.

- Swagger에서 Authorize 없이 POST/PUT/DELETE 테스트 가능
- `dev@handong.ac.kr` 사용자로 자동 로그인 (DB에 없으면 생성)
- **배포·CI(test)에서는 `auth-bypass: false` — 반드시 JWT 사용**

`.env` 예시:

```properties
DEV_AUTH_BYPASS=true
DEV_USER_EMAIL=dev@handong.ac.kr
```

### 일반 (배포·토큰 사용)

- Header: `Authorization: Bearer {accessToken}`
- access token 만료 시: `POST /api/auth/refresh` → 새 토큰 저장
- refresh 실패 시: 로그아웃 처리 후 `/login` 이동

### 역할

| Role | 설명 |
|------|------|
| Guest | 토큰 없음. 🌐 표시 API만 호출 |
| USER | 로그인 사용자. 글·댓글·피드백·모집글 작성 |
| ADMIN | USER 권한 + 타인 글 수정·삭제 가능 |

### 작성자 검증 (SR2)

- 수정·삭제: **서버**에서 `resource.authorId === currentUser.id` 또는 `ADMIN`
- 불일치 시 **403** + `ApiResponse.fail("권한이 없습니다.")`
- 구현: `AuthorValidator` (1단계)

---

## 3. 페이징

목록 API는 Spring `Pageable` 기준 **0-based page**.

**Query parameters**

| Param | Default | Max | 설명 |
|-------|---------|-----|------|
| page | 0 | — | 페이지 번호 |
| size | 20 | 50 | 페이지 크기 |
| sort | 도메인별 | — | 예: `createdAt,desc` |

**Response:** `PageResponse<T>` (1단계)

```json
{
  "content": [],
  "page": 0,
  "size": 20,
  "totalElements": 100,
  "totalPages": 5,
  "first": true,
  "last": false
}
```

---

## 4. Validation

- Request DTO: `javax.validation` (`@NotBlank`, `@Size`, `@Min`, `@Max` 등)
- 실패 시 **400** + `message` (필드 오류 요약)
- FE·BE 동일 규칙 유지 (CMR2)

---

## 5. 예외 · HTTP 매핑

| 예외 | HTTP | message 예시 |
|------|------|----------------|
| `BusinessException(NOT_FOUND)` | 404 | 리소스를 찾을 수 없습니다. |
| `BusinessException(FORBIDDEN)` | 403 | 권한이 없습니다. |
| `BusinessException(UNAUTHORIZED)` | 401 | 로그인이 필요합니다. |
| `AuthException` | 401 | (인증 도메인) |
| `MethodArgumentNotValidException` | 400 | validation 메시지 |
| 기타 | 500 | 서버 오류 (상세는 로그만) |

구현: `ErrorCode` enum + `BusinessException` (1단계)

---

## 6. URL · 네이밍

- 리소스는 **복수형 kebab-case**: `/api/community/posts`
- 하위 리소스: `/posts/{postId}/comments`
- 동사 URL 지양 (`/toggle-like` 대신 `POST .../like`)
- ID path variable: `{postId}`, `{commentId}`, `{projectId}`

---

## 7. 시간 · JSON

- 서버: `LocalDateTime` → JSON **ISO-8601** (`2026-05-20T14:30:00`)
- 타임존: DB `Asia/Seoul`, API는 offset 없는 local datetime (팀 합의 후 ZonedDateTime 전환 가능)

---

## 8. 엔티티 공통

신규 도메인 엔티티는 `BaseTimeEntity` 상속 (1단계):

- `createdAt` — `@CreatedDate`
- `updatedAt` — `@LastModifiedDate`

기존 `User`, `RefreshToken`은 마이그레이션 시점에 맞춰 점진 적용.

---

## 9. Security permitAll (예정)

2단계 커뮤니티 적용 시 `SecurityConfig`에 추가:

```text
GET /api/community/posts/**
GET /api/community/posts/*/comments
GET /api/main/dashboard
```

POST/PUT/DELETE는 authenticated.

---

## 10. 패키지 구조

```text
com.hislink
├── common
│   ├── entity/BaseTimeEntity
│   ├── exception/BusinessException, ErrorCode
│   ├── response/ApiResponse, PageResponse
│   └── security/AuthorValidator
├── domain
│   ├── auth/          (✅)
│   ├── user/          (✅)
│   ├── community/     (🔜 2단계)
│   ├── lab/           (🔜 3단계)
│   └── recruitment/   (🔜 4단계)
```

---

## 11. FE 연동 체크리스트

- [ ] `httpClient`가 `ApiResponse.data`만 반환하는지 확인
- [ ] 목록 화면: `PageResponse.content` 사용
- [ ] 401 시 refresh → 재시도 또는 로그아웃
- [ ] 403 시 사용자 안내 토스트

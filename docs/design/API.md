# HIS-Link REST API 스펙 (초안)

> Base URL: `http://localhost:8080`  
> API prefix: `/api`  
> 응답 래퍼: `ApiResponse<T>` — [CONVENTIONS.md](./CONVENTIONS.md) 참고

**범례:** ✅ 구현 완료 · 🔜 예정 · 👤 로그인 필요 · 🌐 Guest 허용

---

## 1. 인증 (AR1) ✅

| Method | Path | Auth | 설명 |
|--------|------|------|------|
| GET | `http://localhost:8080/oauth2/authorization/google` | 🌐 | Google OAuth 시작 (브라우저에서 이 주소로 접속) |
| GET | `/api/auth/google` | 🌐 | Google 로그인 URL JSON 반환 (Swagger용) |
| POST | `/api/auth/refresh` | 🌐 | refresh token으로 토큰 재발급 |
| POST | `/api/auth/logout` | 🌐 | refresh token 무효화 |
| GET | `/api/auth/me` | 👤 | 현재 사용자 정보 |

### GET `/api/auth/google`

**Response `data`**
```json
{
  "loginUrl": "http://localhost:8080/oauth2/authorization/google",
  "message": "브라우저 주소창에 loginUrl을 입력하거나 링크를 열어 Google 로그인을 진행하세요."
}
```

### POST `/api/auth/logout`

**Query:** `refreshToken` (JWT)

### POST `/api/auth/refresh`

**Query:** `refreshToken` (JWT)

**Response `data`**
```json
{
  "accessToken": "eyJ...",
  "refreshToken": "eyJ...",
  "tokenType": "Bearer",
  "accessTokenExpiresInMs": 3600000
}
```

### GET `/api/auth/me`

**Response `data`**
```json
{
  "id": 1,
  "email": "student@handong.ac.kr",
  "name": "홍길동",
  "profileImageUrl": "https://...",
  "role": "USER"
}
```

---

## 2. 커뮤니티 (AR3) ✅

Base: `/api/community/posts`

| Method | Path | Auth | 설명 |
|--------|------|------|------|
| GET | `/api/community/posts` | 🌐 | 목록 (카테고리·페이징) |
| GET | `/api/community/posts/{postId}` | 🌐 | 상세 (조회수 +1) |
| POST | `/api/community/posts` | 👤 | 글 작성 |
| PUT | `/api/community/posts/{postId}` | 👤 | 수정 (작성자/ADMIN) |
| DELETE | `/api/community/posts/{postId}` | 👤 | 삭제 (작성자/ADMIN) |
| POST | `/api/community/posts/{postId}/like` | 👤 | 좋아요 토글 |
| GET | `/api/community/posts/{postId}/comments` | 🌐 | 댓글 목록 |
| POST | `/api/community/posts/{postId}/comments` | 👤 | 댓글 작성 |
| PUT | `/api/community/comments/{commentId}` | 👤 | 댓글 수정 |
| DELETE | `/api/community/comments/{commentId}` | 👤 | 댓글 삭제 |
| GET | `/api/community/comments/latest` | 🌐 | 최신 댓글 (사이드바) |

### GET `/api/community/posts`

**Query**

| Param | Type | Default | 설명 |
|-------|------|---------|------|
| category | string | (전체) | FREE, QNA, INFO, TROUBLESHOOTING |
| page | int | 0 | 0-based |
| size | int | 20 | max 50 |
| sort | string | createdAt,desc | |

**Response `data`** — `PageResponse<CommunityPostSummaryResponse>`
```json
{
  "content": [
    {
      "id": 1,
      "category": "QNA",
      "title": "Spring Security 질문",
      "contentPreview": "403이 나는데...",
      "author": { "id": 2, "name": "김개발" },
      "viewCount": 10,
      "likeCount": 3,
      "commentCount": 2,
      "createdAt": "2026-05-20T10:00:00"
    }
  ],
  "page": 0,
  "size": 20,
  "totalElements": 42,
  "totalPages": 3,
  "first": true,
  "last": false
}
```

### POST `/api/community/posts`

**Query:** `category`, `title`, `content`

**Response `data`** — `CommunityPostDetailResponse` (201)

### PUT `/api/community/posts/{postId}`

**Query:** `category`, `title`, `content`

### POST `/api/community/posts/{postId}/comments`

**Query:** `content`

### PUT `/api/community/comments/{commentId}`

**Query:** `content`

### GET `/api/community/comments/latest`

**Query:** `size` (default 5, max 20)

**Response `data`** — `LatestCommentResponse[]` (게시글 제목·카테고리·댓글 요약 포함)

### GET `/api/community/posts/{postId}`

**Response `data`**
```json
{
  "id": 1,
  "category": "QNA",
  "title": "제목",
  "content": "본문 전체",
  "author": { "id": 2, "name": "김개발", "profileImageUrl": null },
  "viewCount": 11,
  "likeCount": 3,
  "likedByMe": true,
  "commentCount": 2,
  "createdAt": "2026-05-20T10:00:00",
  "updatedAt": "2026-05-20T11:00:00"
}
```

### POST `/api/community/posts/{postId}/like`

**Response `data`**
```json
{ "liked": true, "likeCount": 4 }
```

---

## 3. User Testing Lab (AR4) ✅

Base: `/api/lab/projects`

| Method | Path | Auth | 설명 |
|--------|------|------|------|
| GET | `/api/lab/projects` | 🌐 | 목록 (정렬·필터) |
| GET | `/api/lab/projects/{projectId}` | 🌐 | 상세 |
| POST | `/api/lab/projects` | 👤 | 프로젝트 등록 |
| PUT | `/api/lab/projects/{projectId}` | 👤 | 수정 |
| DELETE | `/api/lab/projects/{projectId}` | 👤 | 삭제 |
| POST | `/api/lab/projects/{projectId}/like` | 👤 | 좋아요 토글 |
| GET | `/api/lab/projects/{projectId}/feedbacks` | 🌐 | 피드백 목록 |
| POST | `/api/lab/projects/{projectId}/feedbacks` | 👤 | 피드백 작성 |
| PUT | `/api/lab/feedbacks/{feedbackId}` | 👤 | 피드백 수정 |
| DELETE | `/api/lab/feedbacks/{feedbackId}` | 👤 | 피드백 삭제 |

### GET `/api/lab/projects`

**Query:** `sort=LATEST|POPULAR|FEEDBACK`, `keyword`, `techStack`, `page`, `size`

### POST `/api/lab/projects` · PUT `/api/lab/projects/{projectId}`

**Content-Type:** `multipart/form-data`

| Field | 설명 |
|-------|------|
| title, summary | 필수 |
| serviceUrl, githubUrl, testRequest | 선택 |
| techStackIds | 반복 파라미터 |
| images | 신규 이미지 (최대 10장, JPEG/PNG/WEBP/GIF) |
| deleteImageIds | 수정 시 삭제할 이미지 ID (반복) |

정적 파일 URL: `/uploads/projects/{filename}`

### POST `/api/lab/projects/{projectId}/like`

**Response `data`**
```json
{ "liked": true, "likeCount": 12 }
```

### POST `/api/lab/projects/{projectId}/feedbacks`

**Request**
```json
{
  "uiUxScore": 4,
  "functionalityScore": 5,
  "bugReport": "버튼 클릭 시 ...",
  "overallSatisfaction": 4,
  "opinion": "전반적으로 좋습니다",
  "improvementSuggestion": "모바일 반응형 개선"
}
```

---

## 4. 팀 모집 (AR5) ✅

Base: `/api/recruitment/posts`

| Method | Path | Auth | 설명 |
|--------|------|------|------|
| GET | `/api/recruitment/posts` | 🌐 | 목록 (필터) |
| GET | `/api/recruitment/posts/{postId}` | 🌐 | 상세 |
| POST | `/api/recruitment/posts` | 👤 | 모집글 작성 (multipart) |
| PUT | `/api/recruitment/posts/{postId}` | 👤 | 수정 (multipart) |
| DELETE | `/api/recruitment/posts/{postId}` | 👤 | 삭제 |
| GET | `/api/recruitment/posts/{postId}/comments` | 🌐 | 댓글/지원 목록 |
| POST | `/api/recruitment/posts/{postId}/comments` | 👤 | 댓글·지원 |
| PUT | `/api/recruitment/comments/{commentId}` | 👤 | 댓글 수정 |
| DELETE | `/api/recruitment/comments/{commentId}` | 👤 | 댓글 삭제 |

### GET `/api/recruitment/posts`

**Query**

| Param | 설명 |
|-------|------|
| activityType | PROJECT, HACKATHON, CONTEST, COMPETITION (빈 값 = 전체) |
| role | FRONTEND, BACKEND, AI_DATA, DESIGN, PM, OTHER |
| status | OPEN, CLOSED |
| techStack | 기술 스택 이름 |
| page, size, sort | 페이징 (기본 `createdAt,desc`) |

**Response `data`** — `PageResponse<RecruitmentPostSummaryResponse>`
```json
{
  "content": [
    {
      "id": 1,
      "title": "해커톤 프론트 모집",
      "descriptionPreview": "React 경험자...",
      "activityType": "HACKATHON",
      "recruitmentRole": "FRONTEND",
      "status": "OPEN",
      "author": { "id": 2, "name": "김개발" },
      "currentCount": 1,
      "participantLimit": 3,
      "thumbnailUrl": "/uploads/recruitment/abc.jpg",
      "deadline": "2026-06-30T23:59:59",
      "createdAt": "2026-05-20T10:00:00"
    }
  ],
  "page": 0,
  "size": 12,
  "totalElements": 1,
  "totalPages": 1,
  "first": true,
  "last": true
}
```

### GET `/api/recruitment/posts/{postId}`

**Response `data`** — `RecruitmentPostDetailResponse` (description 전체, images[], techStacks[])

### POST `/api/recruitment/posts` · PUT `/api/recruitment/posts/{postId}`

**Content-Type:** `multipart/form-data`

| Field | 설명 |
|-------|------|
| title, description | 필수 |
| activityType | PROJECT, HACKATHON, CONTEST, COMPETITION |
| recruitmentRole | 필수 |
| status | OPEN, CLOSED (작성 시 생략 가능 → OPEN) |
| participantLimit | 필수, ≥1 |
| deadline | ISO-8601 datetime (선택) |
| contactMethod | 선택 |
| techStackIds | 반복 파라미터 |
| images | 신규 이미지 (최대 10장) |
| deleteImageIds | 수정 시 삭제할 이미지 ID (반복) |

### POST `/api/recruitment/posts/{postId}/comments`

**Query:** `content`, `application` (boolean, default false)

- `application=true`: 지원 처리 (`currentCount` 증가, 정원 시 `CLOSED`)
- 작성자·중복 지원·마감글은 400

정적 파일 URL: `/uploads/recruitment/{filename}`

---

## 5. 메인 대시보드 (AR2) ✅

| Method | Path | Auth | 설명 |
|--------|------|------|------|
| GET | `/api/main/dashboard` | 🌐 | 메인 카드용 집계 데이터 |

> `latestRecruitmentPosts`는 AR5 `RecruitmentPostSummaryResponse` 형식 (최대 8건).

**Response `data`**
```json
{
  "latestProjects": [ "..." ],
  "latestCommunityPosts": [ "..." ],
  "latestRecruitmentPosts": [ "..." ],
  "popularProjects": [ "..." ],
  "topFeedbackProjects": [ "..." ]
}
```

각 항목은 목록 API의 Summary DTO와 동일 형식 (N=5~10).

---

## 6. Tech Stack (공통) ✅

| Method | Path | Auth | 설명 |
|--------|------|------|------|
| GET | `/api/tech-stacks` | 🌐 | 전체 스택 목록 (자동완성) |
| POST | `/api/tech-stacks` | 👤 ADMIN | 스택 등록 (선택) |

---

## 7. HTTP 상태 코드

| 코드 | 상황 |
|------|------|
| 200 | 조회·수정 성공 |
| 201 | 생성 성공 |
| 204 | 삭제 성공 (또는 200 + message) |
| 400 | Validation 실패 |
| 401 | 미로그인 / 토큰 무효 |
| 403 | 작성자 불일치 |
| 404 | 리소스 없음 |
| 500 | 서버 오류 |

---

## 8. Swagger

- UI: `http://localhost:8080/swagger-ui/index.html`
- `@Tag`: Community, Lab, Recruitment, Main, Auth 등 도메인별 그룹화 완료
- Lab·Recruitment 작성/수정은 Swagger에서 **multipart** 로 테스트 (상세는 [SWAGGER.md](./SWAGGER.md))

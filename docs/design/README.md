# HIS-Link 설계 문서

| 문서 | 설명 |
|------|------|
| [ERD.md](./ERD.md) | 테이블·관계·Enum·FK 정책 |
| [API.md](./API.md) | REST 경로·Request/Response 초안 |
| [CONVENTIONS.md](./CONVENTIONS.md) | 응답 형식·권한·페이징·예외 규칙 |
| [SWAGGER.md](./SWAGGER.md) | OpenAPI 어노테이션 작성 가이드 |

## 구현 로드맵

| 단계 | 내용 | 상태 |
|------|------|------|
| 0 | 설계 문서 (본 폴더) | ✅ |
| 1 | 공통 인프라 (`BaseTimeEntity`, `BusinessException`, `PageResponse`, `AuthorValidator`) | ✅ |
| 2 | AR3 커뮤니티 | ✅ |
| 3 | AR2 메인 대시보드 | ✅ (Lab·모집 미리보기는 AR4/5 이후) |
| 4 | AR4 User Testing Lab | 🔜 |
| 5 | AR5 팀 모집 | 🔜 |

2단계 시작 전에 본 문서를 팀과 공유·리뷰하는 것을 권장합니다.

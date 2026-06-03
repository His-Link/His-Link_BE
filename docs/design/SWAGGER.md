# Swagger(OpenAPI) 작성 가이드

UI: `http://localhost:8080/swagger-ui/index.html`

새 API를 추가할 때 아래 패턴을 따르면 팀원이 문서만 보고 연동할 수 있습니다.

## 1. Controller (Swagger 친화 — Query Parameter 우선)

일반 작성·수정 API는 **Request body 대신 `@RequestParam`** 을 사용합니다. Swagger UI에서 Parameters만 채우면 됩니다.

**예외 (multipart):** Lab 프로젝트·팀 모집글 작성/수정은 `consumes = MULTIPART_FORM_DATA` 이며, 텍스트 필드는 `@RequestParam`, 이미지는 `images` 파일 필드로 전송합니다.

```java
@Validated
@Tag(name = "...", description = "한글 설명 + http://localhost:8080/oauth2/authorization/google 안내")
@RestController
public class XxxController {

    @Operation(summary = "...", description = "...")
    @PostMapping
    public ApiResponse<...> create(
            @Parameter(description = "...", example = "...", required = true)
            @RequestParam @NotBlank String title,
            ...
    ) { }
}
```

- **Guest 허용 GET**: `@SecurityRequirement` 를 붙이지 않음
- **로그인 필수**: `@SecurityRequirement(name = OpenApiConstants.BEARER_AUTH)`
- **로그인 사용자 주입**: `@CurrentUser AuthenticatedUser user` (Swagger에 body로 안 나옴)
- 상수: `com.hislink.config.OpenApiConstants.BEARER_AUTH`

`@AuthenticationPrincipal`만 쓰면 SpringDoc이 `AuthenticatedUser`를 Request body로 잘못 표시할 수 있습니다.  
`authorities`는 Spring Security 권한(`ROLE_USER` 등)이며, API 입력값이 아닙니다.

## 2. DTO

```java
@Schema(description = "요청/응답 설명")
public class XxxRequest {
    @Schema(description = "필드 설명", example = "예시값")
    private String field;
}
```

## 3. 공통 응답

모든 API는 `ApiResponse<T>` 래퍼를 사용합니다. 실패 시 `success: false`, `message` 확인.

## 4. PR 체크리스트

- [ ] `@Tag` + 각 메서드 `@Operation`
- [ ] 인증 필요 API에 `@SecurityRequirement`
- [ ] Request/Response DTO에 `@Schema`
- [ ] `docs/design/API.md` 해당 섹션 상태 업데이트 (✅)

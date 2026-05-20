package com.hislink.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Schema(description = "Google OAuth 로그인 URL 안내 (Swagger·API용)")
public class GoogleLoginInfoResponse {

    @Schema(
            description = "브라우저 주소창에 입력할 Google 로그인 시작 URL",
            example = "http://localhost:8080/oauth2/authorization/google"
    )
    private final String loginUrl;

    @Schema(description = "사용 방법 안내")
    private final String message;
}

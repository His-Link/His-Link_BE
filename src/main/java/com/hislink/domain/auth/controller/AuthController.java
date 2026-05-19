package com.hislink.domain.auth.controller;

import com.hislink.common.response.ApiResponse;
import com.hislink.config.CurrentUser;
import com.hislink.config.OpenApiConstants;
import com.hislink.domain.auth.dto.GoogleLoginInfoResponse;
import com.hislink.domain.auth.dto.TokenResponse;
import com.hislink.domain.auth.dto.UserResponse;
import com.hislink.domain.auth.security.AuthenticatedUser;
import com.hislink.domain.auth.service.AuthService;
import com.hislink.domain.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotBlank;

@Tag(
        name = "Auth",
        description = "Google OAuth2 로그인 및 JWT(access/refresh) 인증 API (AR1).\n\n"
                + "**Google 로그인 (브라우저):** http://localhost:8080/oauth2/authorization/google\n\n"
                + "Swagger 테스트는 Request body 대신 **Query Parameter** 로 입력합니다."
)
@Validated
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    public static final String GOOGLE_LOGIN_URL = "http://localhost:8080/oauth2/authorization/google";

    private final AuthService authService;

    @Operation(
            summary = "Google OAuth 로그인 URL 조회",
            description = "Swagger Execute로 `loginUrl`을 확인한 뒤, **브라우저 새 탭**에서 해당 주소로 접속하세요.\n\n"
                    + "OAuth는 브라우저 redirect가 필요해 Swagger에서 redirect API를 호출하면 "
                    + "`Failed to fetch`(CORS) 오류가 납니다.\n\n"
                    + "로그인 성공 후 프론트 콜백 URL로 accessToken·refreshToken이 전달됩니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "loginUrl 반환")
    })
    @GetMapping("/google")
    public ApiResponse<GoogleLoginInfoResponse> googleLoginInfo() {
        return ApiResponse.ok(new GoogleLoginInfoResponse(
                GOOGLE_LOGIN_URL,
                "브라우저 주소창에 loginUrl을 입력하거나 링크를 열어 Google 로그인을 진행하세요."
        ));
    }

    @Operation(
            summary = "Access token 재발급",
            description = "refreshToken 파라미터로 새 access/refresh token을 발급합니다. 기존 refresh token은 무효화됩니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "재발급 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "refresh token 무효")
    })
    @PostMapping("/refresh")
    public ApiResponse<TokenResponse> refresh(
            @Parameter(description = "로그인 시 받은 refresh JWT", required = true)
            @RequestParam @NotBlank String refreshToken
    ) {
        return ApiResponse.ok(authService.refreshTokens(refreshToken));
    }

    @Operation(
            summary = "로그아웃",
            description = "refreshToken을 서버에서 삭제합니다. FE localStorage 토큰은 클라이언트에서 별도 제거하세요."
    )
    @PostMapping("/logout")
    public ApiResponse<Void> logout(
            @Parameter(description = "무효화할 refresh JWT", required = true)
            @RequestParam @NotBlank String refreshToken
    ) {
        authService.logout(refreshToken);
        return ApiResponse.okMessage("Logged out successfully.");
    }

    @Operation(
            summary = "내 정보 조회",
            description = "현재 로그인 사용자 정보입니다. local 개발 시 Authorize 없이 dev 사용자로 호출될 수 있습니다."
    )
    @SecurityRequirement(name = OpenApiConstants.BEARER_AUTH)
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "토큰 없음/만료")
    })
    @GetMapping("/me")
    public ApiResponse<UserResponse> me(@CurrentUser AuthenticatedUser authenticatedUser) {
        User user = authService.getUser(authenticatedUser.getUserId());
        return ApiResponse.ok(UserResponse.from(user));
    }
}

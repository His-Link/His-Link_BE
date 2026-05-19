package com.hislink.domain.auth.controller;

import com.hislink.common.response.ApiResponse;
import com.hislink.domain.auth.dto.RefreshTokenRequest;
import com.hislink.domain.auth.dto.TokenResponse;
import com.hislink.domain.auth.dto.UserResponse;
import com.hislink.domain.auth.security.AuthenticatedUser;
import com.hislink.domain.auth.service.AuthService;
import com.hislink.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @GetMapping("/google")
    public void redirectToGoogleLogin(HttpServletResponse response) throws IOException {
        response.sendRedirect("/oauth2/authorization/google");
    }

    @PostMapping("/refresh")
    public ApiResponse<TokenResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return ApiResponse.ok(authService.refreshTokens(request.getRefreshToken()));
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(@Valid @RequestBody RefreshTokenRequest request) {
        authService.logout(request.getRefreshToken());
        return ApiResponse.okMessage("Logged out successfully.");
    }

    @GetMapping("/me")
    public ApiResponse<UserResponse> me(@AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        User user = authService.getUser(authenticatedUser.getUserId());
        return ApiResponse.ok(UserResponse.from(user));
    }
}

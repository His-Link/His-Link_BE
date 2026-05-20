package com.hislink.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "JWT 토큰 발급 응답")
public class TokenResponse {

    private final String accessToken;
    private final String refreshToken;
    private final String tokenType;
    private final long accessTokenExpiresInMs;
}

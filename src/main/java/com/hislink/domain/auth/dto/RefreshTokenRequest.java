package com.hislink.domain.auth.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
public class RefreshTokenRequest {

    @NotBlank(message = "refreshToken is required")
    private String refreshToken;
}

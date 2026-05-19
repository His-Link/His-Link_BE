package com.hislink.domain.community.dto;

import com.hislink.domain.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "작성자 요약 정보")
public class AuthorSummaryResponse {

    @Schema(description = "사용자 ID", example = "1")
    private final Long id;

    @Schema(description = "표시 이름", example = "홍길동")
    private final String name;

    @Schema(description = "프로필 이미지 URL", nullable = true)
    private final String profileImageUrl;

    public static AuthorSummaryResponse from(User user) {
        return new AuthorSummaryResponse(user.getId(), user.getName(), user.getProfileImageUrl());
    }
}

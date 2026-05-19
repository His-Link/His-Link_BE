package com.hislink.domain.community.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "좋아요 토글 결과")
public class LikeToggleResponse {

    @Schema(description = "토글 후 좋아요 상태 (true=좋아요 함)")
    private final boolean liked;

    @Schema(description = "현재 좋아요 수")
    private final int likeCount;
}

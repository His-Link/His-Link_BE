package com.hislink.domain.recruitment.dto;

import com.hislink.domain.recruitment.entity.RecruitmentPostImage;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "팀 모집글 이미지")
public class RecruitmentPostImageResponse {

    private final Long id;
    private final String url;
    private final int sortOrder;

    public static RecruitmentPostImageResponse of(RecruitmentPostImage image, String url) {
        return new RecruitmentPostImageResponse(image.getId(), url, image.getSortOrder());
    }
}

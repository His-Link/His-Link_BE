package com.hislink.domain.lab.dto;

import com.hislink.domain.lab.entity.ProjectImage;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "프로젝트 이미지")
public class ProjectImageResponse {

    private final Long id;
    private final String url;
    private final int sortOrder;

    public static ProjectImageResponse of(ProjectImage image, String url) {
        return new ProjectImageResponse(image.getId(), url, image.getSortOrder());
    }
}

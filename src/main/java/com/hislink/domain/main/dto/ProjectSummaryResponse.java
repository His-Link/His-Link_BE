package com.hislink.domain.main.dto;

import com.hislink.domain.community.dto.AuthorSummaryResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * AR4 Lab 목록 API와 동일한 형태 (메인 대시보드·향후 /api/lab/projects 공유).
 */
@Getter
@AllArgsConstructor
@Schema(description = "User Testing Lab 프로젝트 목록 항목")
public class ProjectSummaryResponse {

    private final Long id;
    private final String title;
    private final String summary;
    private final AuthorSummaryResponse author;
    private final String thumbnailUrl;
    private final int viewCount;
    private final int likeCount;
    private final int feedbackCount;
    private final BigDecimal avgOverallScore;
    private final LocalDateTime createdAt;
}

package com.hislink.domain.main.dto;

import com.hislink.domain.community.dto.AuthorSummaryResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * AR5 모집 목록 API와 동일한 형태 (메인 대시보드·향후 /api/recruitment/posts 공유).
 */
@Getter
@AllArgsConstructor
@Schema(description = "팀 모집글 목록 항목")
public class RecruitmentPostSummaryResponse {

    private final Long id;
    private final String title;
    private final String recruitmentRole;
    private final String status;
    private final AuthorSummaryResponse author;
    private final int currentCount;
    private final int participantLimit;
    private final LocalDateTime deadline;
    private final LocalDateTime createdAt;
}

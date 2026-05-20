package com.hislink.domain.main.dto;

import com.hislink.domain.community.dto.CommunityPostSummaryResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Schema(description = "메인 페이지 대시보드 집계 (AR2)")
public class MainDashboardResponse {

    @Schema(description = "최신 Lab 프로젝트 (AR4)")
    private final List<ProjectSummaryResponse> latestProjects;

    @Schema(description = "최신 커뮤니티 게시글")
    private final List<CommunityPostSummaryResponse> latestCommunityPosts;

    @Schema(description = "최신 팀 모집글 (AR5)")
    private final List<RecruitmentPostSummaryResponse> latestRecruitmentPosts;

    @Schema(description = "인기 Lab 프로젝트 (좋아요 순, AR4)")
    private final List<ProjectSummaryResponse> popularProjects;

    @Schema(description = "피드백 많은 Lab 프로젝트 (AR4)")
    private final List<ProjectSummaryResponse> topFeedbackProjects;
}

package com.hislink.domain.lab.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "Lab 프로젝트 피드백 작성·수정 요청")
public class FeedbackRequest {

    @NotNull
    @Min(1)
    @Max(5)
    @Schema(description = "UI/UX 점수 (1~5)", example = "4")
    private Integer uiUxScore;

    @NotNull
    @Min(1)
    @Max(5)
    @Schema(description = "기능성 점수 (1~5)", example = "5")
    private Integer functionalityScore;

    @Schema(description = "버그 리포트", example = "버튼 클릭 시 500 오류")
    private String bugReport;

    @NotNull
    @Min(1)
    @Max(5)
    @Schema(description = "전반적 만족도 (1~5)", example = "4")
    private Integer overallSatisfaction;

    @Schema(description = "의견", example = "전반적으로 좋습니다")
    private String opinion;

    @Schema(description = "개선 제안", example = "모바일 반응형 개선")
    private String improvementSuggestion;
}

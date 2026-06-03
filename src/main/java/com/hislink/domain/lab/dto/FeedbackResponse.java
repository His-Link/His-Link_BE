package com.hislink.domain.lab.dto;

import com.hislink.domain.community.dto.AuthorSummaryResponse;
import com.hislink.domain.lab.entity.Feedback;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Schema(description = "Lab 프로젝트 피드백")
public class FeedbackResponse {

    private final Long id;
    private final Long projectId;
    private final AuthorSummaryResponse author;
    private final int uiUxScore;
    private final int functionalityScore;
    private final String bugReport;
    private final int overallSatisfaction;
    private final String opinion;
    private final String improvementSuggestion;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public static FeedbackResponse from(Feedback feedback) {
        return new FeedbackResponse(
                feedback.getId(),
                feedback.getProject().getId(),
                AuthorSummaryResponse.from(feedback.getAuthor()),
                feedback.getUiUxScore(),
                feedback.getFunctionalityScore(),
                feedback.getBugReport(),
                feedback.getOverallSatisfaction(),
                feedback.getOpinion(),
                feedback.getImprovementSuggestion(),
                feedback.getCreatedAt(),
                feedback.getUpdatedAt()
        );
    }
}

package com.hislink.domain.main.dto;

import com.hislink.domain.community.dto.AuthorSummaryResponse;
import com.hislink.domain.recruitment.entity.RecruitmentPost;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Schema(description = "팀 모집글 목록 항목")
public class RecruitmentPostSummaryResponse {

    private final Long id;
    private final String title;
    private final String descriptionPreview;
    private final String activityType;
    private final String recruitmentRole;
    private final String status;
    private final AuthorSummaryResponse author;
    private final int currentCount;
    private final int participantLimit;
    private final String thumbnailUrl;
    private final LocalDateTime deadline;
    private final LocalDateTime createdAt;

    public static RecruitmentPostSummaryResponse from(RecruitmentPost post) {
        return new RecruitmentPostSummaryResponse(
                post.getId(),
                post.getTitle(),
                toPreview(post.getDescription()),
                post.getActivityType().name(),
                post.getRecruitmentRole().name(),
                post.getStatus().name(),
                AuthorSummaryResponse.from(post.getAuthor()),
                post.getCurrentCount(),
                post.getParticipantLimit(),
                post.getThumbnailUrl(),
                post.getDeadline(),
                post.getCreatedAt()
        );
    }

    private static String toPreview(String description) {
        if (description == null) {
            return "";
        }
        if (description.length() <= 120) {
            return description;
        }
        return description.substring(0, 120) + "...";
    }
}

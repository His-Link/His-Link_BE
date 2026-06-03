package com.hislink.domain.recruitment.dto;

import com.hislink.domain.community.dto.AuthorSummaryResponse;
import com.hislink.domain.recruitment.entity.RecruitmentPost;
import com.hislink.domain.recruitment.entity.RecruitmentRole;
import com.hislink.domain.recruitment.entity.RecruitmentStatus;
import com.hislink.domain.recruitment.service.RecruitmentFileStorageService;
import com.hislink.domain.techstack.entity.TechStack;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
@Schema(description = "팀 모집글 상세")
public class RecruitmentPostDetailResponse {

    private final Long id;
    private final String title;
    private final String description;
    private final String activityType;
    private final RecruitmentRole recruitmentRole;
    private final RecruitmentStatus status;
    private final AuthorSummaryResponse author;
    private final int currentCount;
    private final int participantLimit;
    private final LocalDateTime deadline;
    private final String contactMethod;
    private final String thumbnailUrl;
    private final List<String> techStacks;
    private final List<RecruitmentPostImageResponse> images;
    private final long commentCount;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public static RecruitmentPostDetailResponse from(
            RecruitmentPost post,
            RecruitmentFileStorageService fileStorage,
            long commentCount
    ) {
        List<String> stackNames = post.getTechStacks().stream()
                .map(TechStack::getName)
                .sorted(Comparator.naturalOrder())
                .collect(Collectors.toList());

        List<RecruitmentPostImageResponse> images = post.getImages().stream()
                .map(image -> RecruitmentPostImageResponse.of(
                        image,
                        fileStorage.toPublicUrl(image.getStoredFileName())
                ))
                .collect(Collectors.toList());

        return new RecruitmentPostDetailResponse(
                post.getId(),
                post.getTitle(),
                post.getDescription(),
                post.getActivityType().name(),
                post.getRecruitmentRole(),
                post.getStatus(),
                AuthorSummaryResponse.from(post.getAuthor()),
                post.getCurrentCount(),
                post.getParticipantLimit(),
                post.getDeadline(),
                post.getContactMethod(),
                post.getThumbnailUrl(),
                stackNames,
                images,
                commentCount,
                post.getCreatedAt(),
                post.getUpdatedAt()
        );
    }
}

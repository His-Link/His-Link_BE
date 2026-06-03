package com.hislink.domain.lab.dto;

import com.hislink.domain.community.dto.AuthorSummaryResponse;
import com.hislink.domain.lab.entity.Project;
import com.hislink.domain.lab.service.ProjectFileStorageService;
import com.hislink.domain.techstack.entity.TechStack;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
@Schema(description = "User Testing Lab 프로젝트 상세")
public class ProjectDetailResponse {

    private final Long id;
    private final String title;
    private final String summary;
    private final String serviceUrl;
    private final String githubUrl;
    private final String testRequest;
    private final AuthorSummaryResponse author;
    private final List<String> techStacks;
    private final List<ProjectImageResponse> images;
    private final int viewCount;
    private final int likeCount;
    private final boolean likedByMe;
    private final int feedbackCount;
    private final BigDecimal avgUiUxScore;
    private final BigDecimal avgFunctionalityScore;
    private final BigDecimal avgOverallScore;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public static ProjectDetailResponse from(Project project, ProjectFileStorageService fileStorage, boolean likedByMe) {
        List<String> stackNames = project.getTechStacks().stream()
                .map(TechStack::getName)
                .sorted(Comparator.naturalOrder())
                .collect(Collectors.toList());

        List<ProjectImageResponse> images = project.getImages().stream()
                .map(image -> ProjectImageResponse.of(
                        image,
                        fileStorage.toPublicUrl(image.getStoredFileName())
                ))
                .collect(Collectors.toList());

        return new ProjectDetailResponse(
                project.getId(),
                project.getTitle(),
                project.getSummary(),
                project.getServiceUrl(),
                project.getGithubUrl(),
                project.getTestRequest(),
                AuthorSummaryResponse.from(project.getAuthor()),
                stackNames,
                images,
                project.getViewCount(),
                project.getLikeCount(),
                likedByMe,
                project.getFeedbackCount(),
                project.getAvgUiUxScore(),
                project.getAvgFunctionalityScore(),
                project.getAvgOverallScore(),
                project.getCreatedAt(),
                project.getUpdatedAt()
        );
    }
}

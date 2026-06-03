package com.hislink.domain.lab.controller;

import com.hislink.common.response.ApiResponse;
import com.hislink.config.CurrentUser;
import com.hislink.config.OpenApiConstants;
import com.hislink.domain.auth.security.AuthenticatedUser;
import com.hislink.domain.lab.dto.FeedbackRequest;
import com.hislink.domain.lab.dto.FeedbackResponse;
import com.hislink.domain.lab.service.FeedbackService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Tag(
        name = "Lab - Feedbacks",
        description = "User Testing Lab 피드백 (AR4). 목록은 Guest, 작성·수정·삭제는 로그인. 프로젝트당 1인 1회 작성."
)
@Validated
@RestController
@RequiredArgsConstructor
public class LabFeedbackController {

    private final FeedbackService feedbackService;

    @Operation(summary = "피드백 목록", description = "프로젝트별 피드백 (Guest 가능)")
    @GetMapping("/api/lab/projects/{projectId}/feedbacks")
    public ApiResponse<List<FeedbackResponse>> findByProject(
            @Parameter(description = "프로젝트 ID", example = "1") @PathVariable Long projectId
    ) {
        return ApiResponse.ok(feedbackService.findByProjectId(projectId));
    }

    @Operation(summary = "피드백 작성", description = "Request Body JSON. 프로젝트당 사용자 1회")
    @SecurityRequirement(name = OpenApiConstants.BEARER_AUTH)
    @PostMapping("/api/lab/projects/{projectId}/feedbacks")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<FeedbackResponse> create(
            @PathVariable Long projectId,
            @Valid @RequestBody FeedbackRequest request,
            @CurrentUser AuthenticatedUser user
    ) {
        return ApiResponse.ok(feedbackService.create(projectId, request, user));
    }

    @Operation(summary = "피드백 수정", description = "본인 피드백만 (ADMIN 예외)")
    @SecurityRequirement(name = OpenApiConstants.BEARER_AUTH)
    @PutMapping("/api/lab/feedbacks/{feedbackId}")
    public ApiResponse<FeedbackResponse> update(
            @PathVariable Long feedbackId,
            @Valid @RequestBody FeedbackRequest request,
            @CurrentUser AuthenticatedUser user
    ) {
        return ApiResponse.ok(feedbackService.update(feedbackId, request, user));
    }

    @Operation(summary = "피드백 삭제", description = "본인 피드백만 (ADMIN 예외)")
    @SecurityRequirement(name = OpenApiConstants.BEARER_AUTH)
    @DeleteMapping("/api/lab/feedbacks/{feedbackId}")
    public ApiResponse<Void> delete(
            @PathVariable Long feedbackId,
            @CurrentUser AuthenticatedUser user
    ) {
        feedbackService.delete(feedbackId, user);
        return ApiResponse.okMessage("피드백이 삭제되었습니다.");
    }
}

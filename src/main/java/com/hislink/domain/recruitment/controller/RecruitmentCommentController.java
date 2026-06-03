package com.hislink.domain.recruitment.controller;

import com.hislink.common.response.ApiResponse;
import com.hislink.config.CurrentUser;
import com.hislink.config.OpenApiConstants;
import com.hislink.domain.auth.security.AuthenticatedUser;
import com.hislink.domain.recruitment.dto.RecruitmentCommentResponse;
import com.hislink.domain.recruitment.service.RecruitmentCommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Tag(name = "Recruitment - Comments", description = "팀 모집 댓글·지원 (AR5)")
@Validated
@RestController
@RequiredArgsConstructor
public class RecruitmentCommentController {

    private final RecruitmentCommentService recruitmentCommentService;

    @Operation(summary = "댓글·지원 목록", description = "Guest 가능")
    @GetMapping("/api/recruitment/posts/{postId}/comments")
    public ApiResponse<List<RecruitmentCommentResponse>> findByPost(@PathVariable Long postId) {
        return ApiResponse.ok(recruitmentCommentService.findByPostId(postId));
    }

    @Operation(summary = "댓글·지원 작성", description = "application=true 시 지원으로 처리")
    @SecurityRequirement(name = OpenApiConstants.BEARER_AUTH)
    @PostMapping("/api/recruitment/posts/{postId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<RecruitmentCommentResponse> create(
            @PathVariable Long postId,
            @RequestParam @NotBlank String content,
            @Parameter(description = "지원 의사") @RequestParam(defaultValue = "false") boolean application,
            @CurrentUser AuthenticatedUser user
    ) {
        return ApiResponse.ok(recruitmentCommentService.create(postId, content, application, user));
    }

    @Operation(summary = "댓글 수정")
    @SecurityRequirement(name = OpenApiConstants.BEARER_AUTH)
    @PutMapping("/api/recruitment/comments/{commentId}")
    public ApiResponse<RecruitmentCommentResponse> update(
            @PathVariable Long commentId,
            @RequestParam @NotBlank String content,
            @CurrentUser AuthenticatedUser user
    ) {
        return ApiResponse.ok(recruitmentCommentService.update(commentId, content, user));
    }

    @Operation(summary = "댓글 삭제")
    @SecurityRequirement(name = OpenApiConstants.BEARER_AUTH)
    @DeleteMapping("/api/recruitment/comments/{commentId}")
    public ApiResponse<Void> delete(
            @PathVariable Long commentId,
            @CurrentUser AuthenticatedUser user
    ) {
        recruitmentCommentService.delete(commentId, user);
        return ApiResponse.okMessage("댓글이 삭제되었습니다.");
    }
}

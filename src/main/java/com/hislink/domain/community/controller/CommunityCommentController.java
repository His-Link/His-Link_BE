package com.hislink.domain.community.controller;

import com.hislink.common.response.ApiResponse;
import com.hislink.config.CurrentUser;
import com.hislink.config.OpenApiConstants;
import com.hislink.domain.auth.security.AuthenticatedUser;
import com.hislink.domain.community.dto.CommentResponse;
import com.hislink.domain.community.service.CommentService;
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

@Tag(
        name = "Community - Comments",
        description = "커뮤니티 댓글 API. Swagger 테스트는 content를 Query Parameter로 입력합니다."
)
@Validated
@RestController
@RequiredArgsConstructor
public class CommunityCommentController {

    private final CommentService commentService;

    @Operation(summary = "댓글 목록", description = "게시글의 댓글 목록 (Guest 가능)")
    @GetMapping("/api/community/posts/{postId}/comments")
    public ApiResponse<List<CommentResponse>> findByPost(
            @Parameter(description = "게시글 ID", example = "1") @PathVariable Long postId
    ) {
        return ApiResponse.ok(commentService.findByPostId(postId));
    }

    @Operation(summary = "댓글 작성", description = "content 파라미터에 댓글 내용 입력")
    @SecurityRequirement(name = OpenApiConstants.BEARER_AUTH)
    @PostMapping("/api/community/posts/{postId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<CommentResponse> create(
            @PathVariable Long postId,
            @Parameter(description = "댓글 내용", example = "저도 같은 문제가 있었어요.", required = true)
            @RequestParam @NotBlank String content,
            @CurrentUser AuthenticatedUser user
    ) {
        return ApiResponse.ok(commentService.create(postId, content, user));
    }

    @Operation(summary = "댓글 수정", description = "본인 댓글만 수정 (ADMIN 예외)")
    @SecurityRequirement(name = OpenApiConstants.BEARER_AUTH)
    @PutMapping("/api/community/comments/{commentId}")
    public ApiResponse<CommentResponse> update(
            @PathVariable Long commentId,
            @Parameter(description = "수정할 내용", required = true) @RequestParam @NotBlank String content,
            @CurrentUser AuthenticatedUser user
    ) {
        return ApiResponse.ok(commentService.update(commentId, content, user));
    }

    @Operation(summary = "댓글 삭제", description = "본인 댓글만 삭제 (ADMIN 예외)")
    @SecurityRequirement(name = OpenApiConstants.BEARER_AUTH)
    @DeleteMapping("/api/community/comments/{commentId}")
    public ApiResponse<Void> delete(
            @PathVariable Long commentId,
            @CurrentUser AuthenticatedUser user
    ) {
        commentService.delete(commentId, user);
        return ApiResponse.okMessage("댓글이 삭제되었습니다.");
    }
}

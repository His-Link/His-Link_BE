package com.hislink.domain.community.controller;

import com.hislink.common.response.ApiResponse;
import com.hislink.common.response.PageResponse;
import com.hislink.config.CurrentUser;
import com.hislink.config.OpenApiConstants;
import com.hislink.domain.auth.security.AuthenticatedUser;
import com.hislink.domain.community.dto.*;
import com.hislink.domain.community.entity.PostCategory;
import com.hislink.domain.community.service.CommunityPostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Tag(
        name = "Community - Posts",
        description = "커뮤니티 게시판 (AR3). Swagger 테스트는 Query Parameter만 입력하면 됩니다. "
                + "Guest: 목록·상세 조회 / 로그인: 작성·수정·삭제·좋아요"
)
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/community/posts")
public class CommunityPostController {

    private final CommunityPostService communityPostService;

    @Operation(
            summary = "게시글 목록 조회",
            description = "카테고리·페이징·정렬로 목록 조회 (Guest 가능). sort 예: createdAt,desc"
    )
    @GetMapping
    public ApiResponse<PageResponse<CommunityPostSummaryResponse>> findAll(
            @Parameter(description = "카테고리 (비우면 전체)", example = "QNA")
            @RequestParam(required = false) PostCategory category,
            @Parameter(description = "페이지 (0부터)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기 (최대 50)", example = "20")
            @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "정렬", example = "createdAt,desc")
            @RequestParam(defaultValue = "createdAt,desc") String sort
    ) {
        return ApiResponse.ok(communityPostService.findAll(category, toPageable(page, size, sort)));
    }

    @Operation(
            summary = "게시글 상세 조회",
            description = "상세 조회 + 조회수 1 증가 (Guest 가능). 토큰 있으면 likedByMe 반영"
    )
    @GetMapping("/{postId}")
    public ApiResponse<CommunityPostDetailResponse> findById(
            @Parameter(description = "게시글 ID", example = "1") @PathVariable Long postId,
            @CurrentUser AuthenticatedUser user
    ) {
        return ApiResponse.ok(communityPostService.findById(postId, user));
    }

    @Operation(summary = "게시글 작성", description = "Query Parameter로 제목·본문·카테고리를 전달합니다.")
    @SecurityRequirement(name = OpenApiConstants.BEARER_AUTH)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<CommunityPostDetailResponse> create(
            @Parameter(description = "카테고리", example = "QNA", required = true)
            @RequestParam @NotNull PostCategory category,
            @Parameter(description = "제목", example = "Spring Security 질문", required = true)
            @RequestParam @NotBlank @Size(max = 200) String title,
            @Parameter(description = "본문", example = "403 오류가 발생합니다.", required = true)
            @RequestParam @NotBlank String content,
            @CurrentUser AuthenticatedUser user
    ) {
        return ApiResponse.ok(communityPostService.create(category, title, content, user));
    }

    @Operation(summary = "게시글 수정", description = "작성자 또는 ADMIN만 수정 가능")
    @SecurityRequirement(name = OpenApiConstants.BEARER_AUTH)
    @PutMapping("/{postId}")
    public ApiResponse<CommunityPostDetailResponse> update(
            @Parameter(description = "게시글 ID", example = "1") @PathVariable Long postId,
            @RequestParam @NotNull PostCategory category,
            @RequestParam @NotBlank @Size(max = 200) String title,
            @RequestParam @NotBlank String content,
            @CurrentUser AuthenticatedUser user
    ) {
        return ApiResponse.ok(communityPostService.update(postId, category, title, content, user));
    }

    @Operation(summary = "게시글 삭제", description = "작성자 또는 ADMIN만 삭제 가능. 댓글·좋아요 함께 삭제")
    @SecurityRequirement(name = OpenApiConstants.BEARER_AUTH)
    @DeleteMapping("/{postId}")
    public ApiResponse<Void> delete(
            @PathVariable Long postId,
            @CurrentUser AuthenticatedUser user
    ) {
        communityPostService.delete(postId, user);
        return ApiResponse.okMessage("게시글이 삭제되었습니다.");
    }

    @Operation(summary = "좋아요 토글", description = "좋아요 추가/취소")
    @SecurityRequirement(name = OpenApiConstants.BEARER_AUTH)
    @PostMapping("/{postId}/like")
    public ApiResponse<LikeToggleResponse> toggleLike(
            @PathVariable Long postId,
            @CurrentUser AuthenticatedUser user
    ) {
        return ApiResponse.ok(communityPostService.toggleLike(postId, user));
    }

    private Pageable toPageable(int page, int size, String sort) {
        String[] parts = sort.split(",");
        String property = parts[0].trim();
        Sort.Direction direction = Sort.Direction.DESC;
        if (parts.length > 1 && "asc".equalsIgnoreCase(parts[1].trim())) {
            direction = Sort.Direction.ASC;
        }
        return PageRequest.of(page, size, Sort.by(direction, property));
    }
}

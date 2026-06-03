package com.hislink.domain.lab.controller;

import com.hislink.common.response.ApiResponse;
import com.hislink.common.response.PageResponse;
import com.hislink.config.CurrentUser;
import com.hislink.config.OpenApiConstants;
import com.hislink.domain.auth.security.AuthenticatedUser;
import com.hislink.domain.community.dto.LikeToggleResponse;
import com.hislink.domain.lab.dto.ProjectDetailResponse;
import com.hislink.domain.lab.entity.ProjectSort;
import com.hislink.domain.lab.service.ProjectService;
import com.hislink.domain.main.dto.ProjectSummaryResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Tag(
        name = "Lab - Projects",
        description = "User Testing Lab 프로젝트 (AR4). 이미지는 multipart images 필드로 업로드 (최대 10장)."
)
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/lab/projects")
public class LabProjectController {

    private final ProjectService projectService;

    @Operation(
            summary = "프로젝트 목록",
            description = "sort: LATEST(기본) | POPULAR | FEEDBACK. techStack은 스택 이름(대소문자 무시)으로 필터"
    )
    @GetMapping
    public ApiResponse<PageResponse<ProjectSummaryResponse>> findAll(
            @Parameter(description = "정렬", example = "LATEST")
            @RequestParam(defaultValue = "LATEST") ProjectSort sort,
            @Parameter(description = "제목·요약 검색어")
            @RequestParam(required = false) String keyword,
            @Parameter(description = "기술 스택 이름", example = "React")
            @RequestParam(required = false) String techStack,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ApiResponse.ok(projectService.findAll(sort, keyword, techStack, pageable));
    }

    @Operation(summary = "프로젝트 상세", description = "조회수 +1")
    @GetMapping("/{projectId}")
    public ApiResponse<ProjectDetailResponse> findById(
            @Parameter(description = "프로젝트 ID", example = "1") @PathVariable Long projectId,
            @CurrentUser AuthenticatedUser user
    ) {
        return ApiResponse.ok(projectService.findById(projectId, user));
    }

    @Operation(summary = "프로젝트 좋아요 토글", description = "좋아요 추가/취소 (AFR1 인기순)")
    @SecurityRequirement(name = OpenApiConstants.BEARER_AUTH)
    @PostMapping("/{projectId}/like")
    public ApiResponse<LikeToggleResponse> toggleLike(
            @PathVariable Long projectId,
            @CurrentUser AuthenticatedUser user
    ) {
        return ApiResponse.ok(projectService.toggleLike(projectId, user));
    }

    @Operation(
            summary = "프로젝트 등록",
            description = "multipart/form-data. images 필드에 파일을 여러 번 첨부. techStackIds는 반복 파라미터."
    )
    @SecurityRequirement(name = OpenApiConstants.BEARER_AUTH)
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ProjectDetailResponse> create(
            @RequestParam @NotBlank @Size(max = 200) String title,
            @RequestParam @NotBlank @Size(max = 500) String summary,
            @RequestParam(required = false) @Size(max = 500) String serviceUrl,
            @RequestParam(required = false) @Size(max = 500) String githubUrl,
            @RequestParam(required = false) String testRequest,
            @RequestParam(required = false) List<Long> techStackIds,
            @RequestParam(value = "images", required = false) MultipartFile[] images,
            @CurrentUser AuthenticatedUser user
    ) {
        return ApiResponse.ok(projectService.create(
                title, summary, serviceUrl, githubUrl, testRequest, techStackIds, images, user
        ));
    }

    @Operation(
            summary = "프로젝트 수정",
            description = "multipart/form-data. deleteImageIds로 기존 이미지 삭제, images로 새 이미지 추가"
    )
    @SecurityRequirement(name = OpenApiConstants.BEARER_AUTH)
    @PutMapping(value = "/{projectId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<ProjectDetailResponse> update(
            @PathVariable Long projectId,
            @RequestParam @NotBlank @Size(max = 200) String title,
            @RequestParam @NotBlank @Size(max = 500) String summary,
            @RequestParam(required = false) @Size(max = 500) String serviceUrl,
            @RequestParam(required = false) @Size(max = 500) String githubUrl,
            @RequestParam(required = false) String testRequest,
            @RequestParam(required = false) List<Long> techStackIds,
            @RequestParam(required = false) List<Long> deleteImageIds,
            @RequestParam(value = "images", required = false) MultipartFile[] images,
            @CurrentUser AuthenticatedUser user
    ) {
        return ApiResponse.ok(projectService.update(
                projectId,
                title,
                summary,
                serviceUrl,
                githubUrl,
                testRequest,
                techStackIds,
                deleteImageIds,
                images,
                user
        ));
    }

    @Operation(summary = "프로젝트 삭제", description = "작성자 또는 ADMIN. 피드백·이미지 파일 함께 삭제")
    @SecurityRequirement(name = OpenApiConstants.BEARER_AUTH)
    @DeleteMapping("/{projectId}")
    public ApiResponse<Void> delete(
            @PathVariable Long projectId,
            @CurrentUser AuthenticatedUser user
    ) {
        projectService.delete(projectId, user);
        return ApiResponse.okMessage("프로젝트가 삭제되었습니다.");
    }
}

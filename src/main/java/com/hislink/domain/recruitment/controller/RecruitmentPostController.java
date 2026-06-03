package com.hislink.domain.recruitment.controller;

import com.hislink.common.response.ApiResponse;
import com.hislink.common.response.PageResponse;
import com.hislink.config.CurrentUser;
import com.hislink.config.OpenApiConstants;
import com.hislink.domain.auth.security.AuthenticatedUser;
import com.hislink.domain.main.dto.RecruitmentPostSummaryResponse;
import com.hislink.domain.recruitment.dto.RecruitmentPostDetailResponse;
import com.hislink.domain.recruitment.entity.RecruitmentActivityType;
import com.hislink.domain.recruitment.entity.RecruitmentRole;
import com.hislink.domain.recruitment.entity.RecruitmentStatus;
import com.hislink.domain.recruitment.service.RecruitmentPostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

@Tag(
        name = "Recruitment - Posts",
        description = "팀 모집 (AR5). multipart images 지원. Guest: 목록·상세 / 로그인: 작성·수정·삭제"
)
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/recruitment/posts")
public class RecruitmentPostController {

    private final RecruitmentPostService recruitmentPostService;

    @Operation(summary = "모집글 목록", description = "활동 유형·역할·상태·기술스택 필터 (Guest 가능)")
    @GetMapping
    public ApiResponse<PageResponse<RecruitmentPostSummaryResponse>> findAll(
            @Parameter(description = "활동 유형: PROJECT, HACKATHON, CONTEST, COMPETITION")
            @RequestParam(required = false) String activityType,
            @Parameter(description = "모집 역할") @RequestParam(required = false) RecruitmentRole role,
            @Parameter(description = "OPEN | CLOSED") @RequestParam(required = false) RecruitmentStatus status,
            @Parameter(description = "기술 스택 이름") @RequestParam(required = false) String techStack,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort
    ) {
        return ApiResponse.ok(recruitmentPostService.findAll(
                RecruitmentActivityType.parseFilter(activityType),
                role,
                status,
                techStack,
                toPageable(page, size, sort)
        ));
    }

    @Operation(summary = "모집글 상세", description = "Guest 가능")
    @GetMapping("/{postId}")
    public ApiResponse<RecruitmentPostDetailResponse> findById(@PathVariable Long postId) {
        return ApiResponse.ok(recruitmentPostService.findById(postId));
    }

    @Operation(summary = "모집글 작성", description = "multipart/form-data. images 필드로 이미지 업로드")
    @SecurityRequirement(name = OpenApiConstants.BEARER_AUTH)
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<RecruitmentPostDetailResponse> create(
            @RequestParam @NotBlank @Size(max = 200) String title,
            @RequestParam @NotBlank String description,
            @RequestParam String activityType,
            @RequestParam @NotNull RecruitmentRole recruitmentRole,
            @RequestParam(required = false) RecruitmentStatus status,
            @RequestParam @Min(1) int participantLimit,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime deadline,
            @RequestParam(required = false) @Size(max = 200) String contactMethod,
            @RequestParam(required = false) List<Long> techStackIds,
            @RequestParam(value = "images", required = false) MultipartFile[] images,
            @CurrentUser AuthenticatedUser user
    ) {
        return ApiResponse.ok(recruitmentPostService.create(
                title,
                description,
                RecruitmentActivityType.parseRequired(activityType),
                recruitmentRole,
                status,
                participantLimit,
                deadline,
                contactMethod,
                techStackIds,
                images,
                user
        ));
    }

    @Operation(summary = "모집글 수정", description = "deleteImageIds로 기존 이미지 삭제, images로 추가")
    @SecurityRequirement(name = OpenApiConstants.BEARER_AUTH)
    @PutMapping(value = "/{postId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<RecruitmentPostDetailResponse> update(
            @PathVariable Long postId,
            @RequestParam @NotBlank @Size(max = 200) String title,
            @RequestParam @NotBlank String description,
            @RequestParam String activityType,
            @RequestParam @NotNull RecruitmentRole recruitmentRole,
            @RequestParam @NotNull RecruitmentStatus status,
            @RequestParam @Min(1) int participantLimit,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime deadline,
            @RequestParam(required = false) @Size(max = 200) String contactMethod,
            @RequestParam(required = false) List<Long> techStackIds,
            @RequestParam(required = false) List<Long> deleteImageIds,
            @RequestParam(value = "images", required = false) MultipartFile[] images,
            @CurrentUser AuthenticatedUser user
    ) {
        return ApiResponse.ok(recruitmentPostService.update(
                postId,
                title,
                description,
                RecruitmentActivityType.parseRequired(activityType),
                recruitmentRole,
                status,
                participantLimit,
                deadline,
                contactMethod,
                techStackIds,
                deleteImageIds,
                images,
                user
        ));
    }

    @Operation(summary = "모집글 삭제")
    @SecurityRequirement(name = OpenApiConstants.BEARER_AUTH)
    @DeleteMapping("/{postId}")
    public ApiResponse<Void> delete(@PathVariable Long postId, @CurrentUser AuthenticatedUser user) {
        recruitmentPostService.delete(postId, user);
        return ApiResponse.okMessage("모집글이 삭제되었습니다.");
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

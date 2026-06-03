package com.hislink.domain.techstack.controller;

import com.hislink.common.response.ApiResponse;
import com.hislink.config.CurrentUser;
import com.hislink.config.OpenApiConstants;
import com.hislink.domain.auth.security.AuthenticatedUser;
import com.hislink.domain.techstack.dto.TechStackResponse;
import com.hislink.domain.techstack.service.TechStackService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Tag(name = "Tech Stack", description = "기술 스택 목록·등록 (Lab·모집 공통)")
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tech-stacks")
public class TechStackController {

    private final TechStackService techStackService;

    @Operation(summary = "기술 스택 전체 목록", description = "자동완성용 (Guest 가능)")
    @GetMapping
    public ApiResponse<List<TechStackResponse>> findAll() {
        return ApiResponse.ok(techStackService.findAll());
    }

    @Operation(summary = "기술 스택 등록", description = "ADMIN만 등록 가능")
    @SecurityRequirement(name = OpenApiConstants.BEARER_AUTH)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<TechStackResponse> create(
            @Parameter(description = "스택 이름", example = "Kotlin", required = true)
            @RequestParam @NotBlank @Size(max = 50) String name,
            @CurrentUser AuthenticatedUser user
    ) {
        return ApiResponse.ok(techStackService.create(name, user));
    }
}

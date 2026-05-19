package com.hislink.domain.sample.controller;

import com.hislink.common.response.ApiResponse;
import com.hislink.config.OpenApiConstants;
import com.hislink.domain.sample.dto.SampleItemResponse;
import com.hislink.domain.sample.service.SampleItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Tag(
        name = "Sample",
        description = "개발용 샘플 API. Query Parameter로 테스트합니다."
)
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/sample-items")
@SecurityRequirement(name = OpenApiConstants.BEARER_AUTH)
public class SampleItemController {

    private final SampleItemService sampleItemService;

    @Operation(summary = "샘플 생성")
    @PostMapping
    public ApiResponse<SampleItemResponse> create(
            @Parameter(description = "제목", example = "테스트", required = true)
            @RequestParam @NotBlank String title,
            @Parameter(description = "설명", example = "샘플 설명", required = true)
            @RequestParam @NotBlank String description
    ) {
        return ApiResponse.ok(sampleItemService.create(title, description));
    }

    @Operation(summary = "샘플 전체 조회")
    @GetMapping
    public ApiResponse<List<SampleItemResponse>> findAll() {
        return ApiResponse.ok(sampleItemService.findAll());
    }

    @Operation(summary = "샘플 단건 조회")
    @GetMapping("/{id}")
    public ApiResponse<SampleItemResponse> findById(@PathVariable Long id) {
        return ApiResponse.ok(sampleItemService.findById(id));
    }

    @Operation(summary = "샘플 수정")
    @PutMapping("/{id}")
    public ApiResponse<SampleItemResponse> update(
            @PathVariable Long id,
            @RequestParam @NotBlank String title,
            @RequestParam @NotBlank String description
    ) {
        return ApiResponse.ok(sampleItemService.update(id, title, description));
    }

    @Operation(summary = "샘플 삭제")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        sampleItemService.delete(id);
        return ApiResponse.okMessage("Deleted successfully");
    }
}

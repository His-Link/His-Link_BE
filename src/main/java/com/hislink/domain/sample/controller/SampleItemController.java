package com.hislink.domain.sample.controller;

import com.hislink.common.response.ApiResponse;
import com.hislink.domain.sample.dto.SampleItemCreateRequest;
import com.hislink.domain.sample.dto.SampleItemResponse;
import com.hislink.domain.sample.dto.SampleItemUpdateRequest;
import com.hislink.domain.sample.service.SampleItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/sample-items")
public class SampleItemController {

    private final SampleItemService sampleItemService;

    @PostMapping
    public ApiResponse<SampleItemResponse> create(@Valid @RequestBody SampleItemCreateRequest request) {
        return ApiResponse.ok(sampleItemService.create(request));
    }

    @GetMapping
    public ApiResponse<List<SampleItemResponse>> findAll() {
        return ApiResponse.ok(sampleItemService.findAll());
    }

    @GetMapping("/{id}")
    public ApiResponse<SampleItemResponse> findById(@PathVariable Long id) {
        return ApiResponse.ok(sampleItemService.findById(id));
    }

    @PutMapping("/{id}")
    public ApiResponse<SampleItemResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody SampleItemUpdateRequest request
    ) {
        return ApiResponse.ok(sampleItemService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        sampleItemService.delete(id);
        return ApiResponse.okMessage("Deleted successfully");
    }
}

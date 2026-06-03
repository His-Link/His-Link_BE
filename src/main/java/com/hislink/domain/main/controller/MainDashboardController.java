package com.hislink.domain.main.controller;

import com.hislink.common.response.ApiResponse;
import com.hislink.domain.main.dto.MainDashboardResponse;
import com.hislink.domain.main.service.MainDashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
        name = "Main",
        description = "메인 대시보드 (AR2). Guest 조회 가능. Lab은 AR4 데이터 반영. 모집(AR5)은 아직 빈 목록입니다."
)
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/main")
public class MainDashboardController {

    private final MainDashboardService mainDashboardService;

    @Operation(
            summary = "메인 대시보드 조회",
            description = "메인 페이지 카드용 미리보기 데이터입니다. "
                    + "latestCommunityPosts는 최신 8건, "
                    + "Lab·모집 관련 목록은 도메인 구현 후 채워집니다."
    )
    @GetMapping("/dashboard")
    public ApiResponse<MainDashboardResponse> getDashboard() {
        return ApiResponse.ok(mainDashboardService.getDashboard());
    }
}

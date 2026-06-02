package com.hislink.domain.main.service;

import com.hislink.domain.community.dto.CommunityPostSummaryResponse;
import com.hislink.domain.community.entity.CommunityPost;
import com.hislink.domain.community.repository.CommentRepository;
import com.hislink.domain.community.repository.CommunityPostRepository;
import com.hislink.domain.lab.service.ProjectService;
import com.hislink.domain.main.dto.MainDashboardResponse;
import com.hislink.domain.main.dto.ProjectSummaryResponse;
import com.hislink.domain.main.dto.RecruitmentPostSummaryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MainDashboardService {

    static final int PREVIEW_SIZE = 8;

    private final CommunityPostRepository communityPostRepository;
    private final CommentRepository commentRepository;
    private final ProjectService projectService;

    @Transactional(readOnly = true)
    public MainDashboardResponse getDashboard() {
        return new MainDashboardResponse(
                projectService.findLatestPreview(PREVIEW_SIZE),
                fetchLatestCommunityPosts(),
                emptyRecruitmentPosts(),
                projectService.findPopularPreview(PREVIEW_SIZE),
                projectService.findTopFeedbackPreview(PREVIEW_SIZE)
        );
    }

    private List<CommunityPostSummaryResponse> fetchLatestCommunityPosts() {
        PageRequest pageable = PageRequest.of(0, PREVIEW_SIZE, Sort.by(Sort.Direction.DESC, "createdAt"));
        return communityPostRepository.findAllWithAuthor(pageable).getContent().stream()
                .map(this::toCommunitySummary)
                .collect(Collectors.toList());
    }

    private CommunityPostSummaryResponse toCommunitySummary(CommunityPost post) {
        return CommunityPostSummaryResponse.from(
                post,
                commentRepository.countByPostId(post.getId())
        );
    }

    private static List<RecruitmentPostSummaryResponse> emptyRecruitmentPosts() {
        return Collections.emptyList();
    }
}

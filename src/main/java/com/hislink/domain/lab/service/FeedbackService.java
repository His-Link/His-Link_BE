package com.hislink.domain.lab.service;

import com.hislink.common.exception.BusinessException;
import com.hislink.common.exception.ErrorCode;
import com.hislink.common.security.AuthorValidator;
import com.hislink.domain.auth.security.AuthenticatedUser;
import com.hislink.domain.lab.dto.FeedbackRequest;
import com.hislink.domain.lab.dto.FeedbackResponse;
import com.hislink.domain.lab.entity.Feedback;
import com.hislink.domain.lab.entity.Project;
import com.hislink.domain.lab.repository.FeedbackRepository;
import com.hislink.domain.user.entity.User;
import com.hislink.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final ProjectService projectService;
    private final UserRepository userRepository;
    private final AuthorValidator authorValidator;

    @Transactional(readOnly = true)
    public List<FeedbackResponse> findByProjectId(Long projectId) {
        projectService.getProjectWithDetails(projectId);
        return feedbackRepository.findByProjectIdWithAuthor(projectId).stream()
                .map(FeedbackResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public FeedbackResponse create(Long projectId, FeedbackRequest request, AuthenticatedUser user) {
        authorValidator.requireAuthenticated(user);
        Project project = projectService.getProjectWithDetails(projectId);

        if (feedbackRepository.existsByProjectIdAndAuthorId(projectId, user.getUserId())) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "이미 이 프로젝트에 피드백을 작성했습니다.");
        }

        User author = getUser(user.getUserId());
        Feedback feedback = feedbackRepository.save(Feedback.builder()
                .project(project)
                .author(author)
                .uiUxScore(request.getUiUxScore())
                .functionalityScore(request.getFunctionalityScore())
                .bugReport(request.getBugReport())
                .overallSatisfaction(request.getOverallSatisfaction())
                .opinion(request.getOpinion())
                .improvementSuggestion(request.getImprovementSuggestion())
                .build());

        projectService.recalculateFeedbackAggregates(projectId);
        return FeedbackResponse.from(feedback);
    }

    @Transactional
    public FeedbackResponse update(Long feedbackId, FeedbackRequest request, AuthenticatedUser user) {
        Feedback feedback = getFeedback(feedbackId);
        authorValidator.validateAuthor(feedback.getAuthor().getId(), user);
        feedback.update(
                request.getUiUxScore(),
                request.getFunctionalityScore(),
                request.getBugReport(),
                request.getOverallSatisfaction(),
                request.getOpinion(),
                request.getImprovementSuggestion()
        );
        Long projectId = feedback.getProject().getId();
        projectService.recalculateFeedbackAggregates(projectId);
        return FeedbackResponse.from(feedback);
    }

    @Transactional
    public void delete(Long feedbackId, AuthenticatedUser user) {
        Feedback feedback = getFeedback(feedbackId);
        authorValidator.validateAuthor(feedback.getAuthor().getId(), user);
        Long projectId = feedback.getProject().getId();
        feedbackRepository.delete(feedback);
        projectService.recalculateFeedbackAggregates(projectId);
    }

    private Feedback getFeedback(Long feedbackId) {
        return feedbackRepository.findByIdWithAuthor(feedbackId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "피드백을 찾을 수 없습니다."));
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "사용자를 찾을 수 없습니다."));
    }
}

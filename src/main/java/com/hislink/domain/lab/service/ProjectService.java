package com.hislink.domain.lab.service;

import com.hislink.common.exception.BusinessException;
import com.hislink.common.exception.ErrorCode;
import com.hislink.common.response.PageResponse;
import com.hislink.common.security.AuthorValidator;
import com.hislink.config.UploadProperties;
import com.hislink.domain.auth.security.AuthenticatedUser;
import com.hislink.domain.community.dto.LikeToggleResponse;
import com.hislink.domain.lab.dto.ProjectDetailResponse;
import com.hislink.domain.lab.entity.Project;
import com.hislink.domain.lab.entity.ProjectImage;
import com.hislink.domain.lab.entity.ProjectLike;
import com.hislink.domain.lab.entity.ProjectSort;
import com.hislink.domain.lab.repository.FeedbackRepository;
import com.hislink.domain.lab.repository.ProjectLikeRepository;
import com.hislink.domain.lab.repository.ProjectRepository;
import com.hislink.domain.lab.repository.ProjectSpecifications;
import com.hislink.domain.main.dto.ProjectSummaryResponse;
import com.hislink.domain.techstack.entity.TechStack;
import com.hislink.domain.techstack.service.TechStackService;
import com.hislink.domain.user.entity.User;
import com.hislink.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private static final int MAX_PAGE_SIZE = 50;

    private final ProjectRepository projectRepository;
    private final ProjectLikeRepository projectLikeRepository;
    private final FeedbackRepository feedbackRepository;
    private final UserRepository userRepository;
    private final TechStackService techStackService;
    private final AuthorValidator authorValidator;
    private final ProjectFileStorageService fileStorage;
    private final UploadProperties uploadProperties;

    @Transactional
    public ProjectDetailResponse create(
            String title,
            String summary,
            String serviceUrl,
            String githubUrl,
            String testRequest,
            List<Long> techStackIds,
            MultipartFile[] images,
            AuthenticatedUser user
    ) {
        authorValidator.requireAuthenticated(user);
        User author = getUser(user.getUserId());
        Set<TechStack> techStacks = techStackService.resolveTechStacks(techStackIds);

        Project project = Project.builder()
                .author(author)
                .title(title)
                .summary(summary)
                .serviceUrl(serviceUrl)
                .githubUrl(githubUrl)
                .testRequest(testRequest)
                .build();
        project.replaceTechStacks(techStacks);
        projectRepository.save(project);

        addImages(project, images);
        syncCoverUrl(project);

        return toDetailResponse(project, false);
    }

    @Transactional(readOnly = true)
    public PageResponse<ProjectSummaryResponse> findAll(
            ProjectSort sort,
            String keyword,
            String techStack,
            Pageable pageable
    ) {
        Pageable safePageable = clampPageable(pageable, sort);
        Specification<Project> spec = Specification.where(ProjectSpecifications.fetchAuthor())
                .and(ProjectSpecifications.keywordContains(keyword))
                .and(ProjectSpecifications.hasTechStackName(techStack));

        Page<Project> page = projectRepository.findAll(spec, safePageable);
        return PageResponse.from(page.map(ProjectSummaryResponse::from));
    }

    @Transactional
    public ProjectDetailResponse findById(Long projectId, AuthenticatedUser user) {
        Project project = getProjectWithDetails(projectId);
        project.increaseViewCount();
        return toDetailResponse(project, isLikedByMe(projectId, user));
    }

    @Transactional
    public LikeToggleResponse toggleLike(Long projectId, AuthenticatedUser user) {
        authorValidator.requireAuthenticated(user);
        Project project = getProjectWithDetails(projectId);

        return projectLikeRepository.findByProjectIdAndUserId(projectId, user.getUserId())
                .map(existing -> {
                    projectLikeRepository.delete(existing);
                    project.decreaseLikeCount();
                    return new LikeToggleResponse(false, project.getLikeCount());
                })
                .orElseGet(() -> {
                    User liker = getUser(user.getUserId());
                    projectLikeRepository.save(ProjectLike.builder().project(project).user(liker).build());
                    project.increaseLikeCount();
                    return new LikeToggleResponse(true, project.getLikeCount());
                });
    }

    @Transactional
    public ProjectDetailResponse update(
            Long projectId,
            String title,
            String summary,
            String serviceUrl,
            String githubUrl,
            String testRequest,
            List<Long> techStackIds,
            List<Long> deleteImageIds,
            MultipartFile[] images,
            AuthenticatedUser user
    ) {
        Project project = getProjectWithDetails(projectId);
        authorValidator.validateAuthor(project.getAuthor().getId(), user);
        project.update(title, summary, serviceUrl, githubUrl, testRequest);
        project.replaceTechStacks(techStackService.resolveTechStacks(techStackIds));

        removeImages(project, deleteImageIds);
        addImages(project, images);
        syncCoverUrl(project);

        return toDetailResponse(project, isLikedByMe(projectId, user));
    }

    @Transactional
    public void delete(Long projectId, AuthenticatedUser user) {
        Project project = getProjectWithDetails(projectId);
        authorValidator.validateAuthor(project.getAuthor().getId(), user);
        deleteAllImageFiles(project);
        projectRepository.delete(project);
    }

    @Transactional(readOnly = true)
    public List<ProjectSummaryResponse> findLatestPreview(int size) {
        Pageable pageable = PageRequest.of(0, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return projectRepository.findAllWithAuthor(pageable).getContent().stream()
                .map(ProjectSummaryResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProjectSummaryResponse> findPopularPreview(int size) {
        Pageable pageable = PageRequest.of(0, size, Sort.by(Sort.Direction.DESC, "likeCount"));
        return projectRepository.findAllWithAuthor(pageable).getContent().stream()
                .map(ProjectSummaryResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProjectSummaryResponse> findTopFeedbackPreview(int size) {
        Pageable pageable = PageRequest.of(0, size, Sort.by(Sort.Direction.DESC, "feedbackCount"));
        return projectRepository.findAllWithAuthor(pageable).getContent().stream()
                .map(ProjectSummaryResponse::from)
                .collect(Collectors.toList());
    }

    public Project getProjectWithDetails(Long projectId) {
        return projectRepository.findByIdWithDetails(projectId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "프로젝트를 찾을 수 없습니다."));
    }

    @Transactional
    public void recalculateFeedbackAggregates(Long projectId) {
        Project project = getProjectWithDetails(projectId);
        List<com.hislink.domain.lab.entity.Feedback> feedbacks = feedbackRepository.findByProjectId(projectId);

        int count = feedbacks.size();
        if (count == 0) {
            project.applyFeedbackAggregates(0, null, null, null);
            return;
        }

        List<Integer> uiScores = feedbacks.stream()
                .map(com.hislink.domain.lab.entity.Feedback::getUiUxScore)
                .collect(Collectors.toList());
        List<Integer> functionalityScores = feedbacks.stream()
                .map(com.hislink.domain.lab.entity.Feedback::getFunctionalityScore)
                .collect(Collectors.toList());
        List<Integer> overallScores = feedbacks.stream()
                .map(com.hislink.domain.lab.entity.Feedback::getOverallSatisfaction)
                .collect(Collectors.toList());

        project.applyFeedbackAggregates(
                count,
                Project.averageScore(uiScores),
                Project.averageScore(functionalityScores),
                Project.averageScore(overallScores)
        );
    }

    private void addImages(Project project, MultipartFile[] images) {
        if (images == null || images.length == 0) {
            return;
        }

        List<MultipartFile> validFiles = new ArrayList<>();
        for (MultipartFile file : images) {
            if (file != null && !file.isEmpty()) {
                validFiles.add(file);
            }
        }
        if (validFiles.isEmpty()) {
            return;
        }

        int currentCount = project.getImages().size();
        if (currentCount + validFiles.size() > uploadProperties.getMaxImagesPerProject()) {
            throw new BusinessException(
                    ErrorCode.BAD_REQUEST,
                    "프로젝트 이미지는 최대 " + uploadProperties.getMaxImagesPerProject() + "장까지 등록할 수 있습니다."
            );
        }

        int sortOrder = currentCount;
        for (MultipartFile file : validFiles) {
            String storedFileName = fileStorage.store(file);
            project.addImage(ProjectImage.builder()
                    .storedFileName(storedFileName)
                    .sortOrder(sortOrder++)
                    .build());
        }
    }

    private void removeImages(Project project, List<Long> deleteImageIds) {
        if (deleteImageIds == null || deleteImageIds.isEmpty()) {
            return;
        }

        List<ProjectImage> targets = project.getImages().stream()
                .filter(image -> deleteImageIds.contains(image.getId()))
                .collect(Collectors.toList());

        for (ProjectImage image : targets) {
            fileStorage.delete(image.getStoredFileName());
            project.getImages().remove(image);
        }

        reorderImages(project);
    }

    private void reorderImages(Project project) {
        List<ProjectImage> sorted = project.getImages().stream()
                .sorted(Comparator.comparingInt(ProjectImage::getSortOrder))
                .collect(Collectors.toList());
        for (int i = 0; i < sorted.size(); i++) {
            sorted.get(i).setSortOrder(i);
        }
    }

    private void syncCoverUrl(Project project) {
        if (project.getImages().isEmpty()) {
            project.setThumbnailUrl(null);
            return;
        }
        ProjectImage first = project.getImages().stream()
                .min(Comparator.comparingInt(ProjectImage::getSortOrder))
                .orElse(project.getImages().get(0));
        project.setThumbnailUrl(fileStorage.toPublicUrl(first.getStoredFileName()));
    }

    private void deleteAllImageFiles(Project project) {
        for (ProjectImage image : project.getImages()) {
            fileStorage.delete(image.getStoredFileName());
        }
    }

    private ProjectDetailResponse toDetailResponse(Project project, boolean likedByMe) {
        return ProjectDetailResponse.from(project, fileStorage, likedByMe);
    }

    private boolean isLikedByMe(Long projectId, AuthenticatedUser user) {
        return user != null && projectLikeRepository.existsByProjectIdAndUserId(projectId, user.getUserId());
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "사용자를 찾을 수 없습니다."));
    }

    private Pageable clampPageable(Pageable pageable, ProjectSort sort) {
        int size = Math.min(Math.max(pageable.getPageSize(), 1), MAX_PAGE_SIZE);
        int page = Math.max(pageable.getPageNumber(), 0);
        Sort resolvedSort = resolveSort(sort);
        return PageRequest.of(page, size, resolvedSort);
    }

    private Sort resolveSort(ProjectSort sort) {
        if (sort == null || sort == ProjectSort.LATEST) {
            return Sort.by(Sort.Direction.DESC, "createdAt");
        }
        if (sort == ProjectSort.POPULAR) {
            return Sort.by(Sort.Direction.DESC, "likeCount");
        }
        return Sort.by(Sort.Direction.DESC, "feedbackCount");
    }
}

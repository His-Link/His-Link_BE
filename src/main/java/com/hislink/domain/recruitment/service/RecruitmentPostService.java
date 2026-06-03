package com.hislink.domain.recruitment.service;

import com.hislink.common.exception.BusinessException;
import com.hislink.common.exception.ErrorCode;
import com.hislink.common.response.PageResponse;
import com.hislink.common.security.AuthorValidator;
import com.hislink.common.util.PageableSupport;
import com.hislink.config.UploadProperties;
import com.hislink.domain.auth.security.AuthenticatedUser;
import com.hislink.domain.main.dto.RecruitmentPostSummaryResponse;
import com.hislink.domain.recruitment.dto.RecruitmentPostDetailResponse;
import com.hislink.domain.recruitment.entity.RecruitmentActivityType;
import com.hislink.domain.recruitment.entity.RecruitmentPost;
import com.hislink.domain.recruitment.entity.RecruitmentPostImage;
import com.hislink.domain.recruitment.entity.RecruitmentRole;
import com.hislink.domain.recruitment.entity.RecruitmentStatus;
import com.hislink.domain.recruitment.repository.RecruitmentCommentRepository;
import com.hislink.domain.recruitment.repository.RecruitmentPostRepository;
import com.hislink.domain.recruitment.repository.RecruitmentPostSpecifications;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecruitmentPostService {

    private static final int MAX_PAGE_SIZE = 50;

    private final RecruitmentPostRepository recruitmentPostRepository;
    private final RecruitmentCommentRepository recruitmentCommentRepository;
    private final UserRepository userRepository;
    private final TechStackService techStackService;
    private final AuthorValidator authorValidator;
    private final RecruitmentFileStorageService fileStorage;
    private final UploadProperties uploadProperties;

    @Transactional
    public RecruitmentPostDetailResponse create(
            String title,
            String description,
            RecruitmentActivityType activityType,
            RecruitmentRole recruitmentRole,
            RecruitmentStatus status,
            int participantLimit,
            LocalDateTime deadline,
            String contactMethod,
            List<Long> techStackIds,
            MultipartFile[] images,
            AuthenticatedUser user
    ) {
        authorValidator.requireAuthenticated(user);
        validateParticipantLimit(participantLimit);

        User author = getUser(user.getUserId());
        Set<TechStack> techStacks = techStackService.resolveTechStacks(techStackIds);

        RecruitmentPost post = RecruitmentPost.builder()
                .author(author)
                .title(title)
                .description(description)
                .activityType(activityType)
                .recruitmentRole(recruitmentRole)
                .status(status != null ? status : RecruitmentStatus.OPEN)
                .participantLimit(participantLimit)
                .deadline(deadline)
                .contactMethod(contactMethod)
                .build();
        post.replaceTechStacks(techStacks);
        recruitmentPostRepository.save(post);

        addImages(post, images);
        syncThumbnailUrl(post);

        return toDetailResponse(post, 0);
    }

    @Transactional(readOnly = true)
    public PageResponse<RecruitmentPostSummaryResponse> findAll(
            RecruitmentActivityType activityType,
            RecruitmentRole role,
            RecruitmentStatus status,
            String techStack,
            Pageable pageable
    ) {
        Pageable safePageable = PageableSupport.sanitize(
                pageable,
                MAX_PAGE_SIZE,
                Set.of("createdAt", "deadline", "currentCount", "title"),
                "createdAt",
                Sort.Direction.DESC
        );

        Specification<RecruitmentPost> spec = Specification
                .where(RecruitmentPostSpecifications.withAuthor())
                .and(RecruitmentPostSpecifications.hasActivityType(activityType))
                .and(RecruitmentPostSpecifications.hasRole(role))
                .and(RecruitmentPostSpecifications.hasStatus(status))
                .and(RecruitmentPostSpecifications.hasTechStackName(techStack));

        Page<RecruitmentPost> page = recruitmentPostRepository.findAll(spec, safePageable);
        return PageResponse.from(page.map(RecruitmentPostSummaryResponse::from));
    }

    @Transactional(readOnly = true)
    public RecruitmentPostDetailResponse findById(Long postId) {
        RecruitmentPost post = getPostWithDetails(postId);
        long commentCount = recruitmentCommentRepository.countByPostId(postId);
        return toDetailResponse(post, commentCount);
    }

    @Transactional
    public RecruitmentPostDetailResponse update(
            Long postId,
            String title,
            String description,
            RecruitmentActivityType activityType,
            RecruitmentRole recruitmentRole,
            RecruitmentStatus status,
            int participantLimit,
            LocalDateTime deadline,
            String contactMethod,
            List<Long> techStackIds,
            List<Long> deleteImageIds,
            MultipartFile[] images,
            AuthenticatedUser user
    ) {
        RecruitmentPost post = getPostWithDetails(postId);
        authorValidator.validateAuthor(post.getAuthor().getId(), user);
        validateParticipantLimit(participantLimit);

        if (participantLimit < post.getCurrentCount()) {
            throw new BusinessException(
                    ErrorCode.BAD_REQUEST,
                    "모집 인원은 현재 참여 수(" + post.getCurrentCount() + ")보다 작을 수 없습니다."
            );
        }

        post.update(
                title,
                description,
                activityType,
                recruitmentRole,
                status,
                participantLimit,
                deadline,
                contactMethod
        );
        post.replaceTechStacks(techStackService.resolveTechStacks(techStackIds));
        removeImages(post, deleteImageIds);
        addImages(post, images);
        syncThumbnailUrl(post);

        return toDetailResponse(post, recruitmentCommentRepository.countByPostId(postId));
    }

    @Transactional
    public void delete(Long postId, AuthenticatedUser user) {
        RecruitmentPost post = getPostWithDetails(postId);
        authorValidator.validateAuthor(post.getAuthor().getId(), user);
        deleteAllImageFiles(post);
        recruitmentPostRepository.delete(post);
    }

    @Transactional(readOnly = true)
    public List<RecruitmentPostSummaryResponse> findLatestPreview(int size) {
        PageRequest pageable = PageRequest.of(0, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Specification<RecruitmentPost> spec = RecruitmentPostSpecifications.withAuthor();
        return recruitmentPostRepository.findAll(spec, pageable).getContent().stream()
                .map(RecruitmentPostSummaryResponse::from)
                .collect(Collectors.toList());
    }

    public RecruitmentPost getPostWithDetails(Long postId) {
        return recruitmentPostRepository.findByIdWithDetails(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "모집글을 찾을 수 없습니다."));
    }

    private void addImages(RecruitmentPost post, MultipartFile[] images) {
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

        int currentCount = post.getImages().size();
        int max = uploadProperties.getMaxImagesPerRecruitmentPost();
        if (currentCount + validFiles.size() > max) {
            throw new BusinessException(
                    ErrorCode.BAD_REQUEST,
                    "모집글 이미지는 최대 " + max + "장까지 등록할 수 있습니다."
            );
        }

        int sortOrder = currentCount;
        for (MultipartFile file : validFiles) {
            String storedFileName = fileStorage.store(file);
            post.addImage(RecruitmentPostImage.builder()
                    .storedFileName(storedFileName)
                    .sortOrder(sortOrder++)
                    .build());
        }
    }

    private void removeImages(RecruitmentPost post, List<Long> deleteImageIds) {
        if (deleteImageIds == null || deleteImageIds.isEmpty()) {
            return;
        }

        List<RecruitmentPostImage> targets = post.getImages().stream()
                .filter(image -> deleteImageIds.contains(image.getId()))
                .collect(Collectors.toList());

        for (RecruitmentPostImage image : targets) {
            fileStorage.delete(image.getStoredFileName());
            post.getImages().remove(image);
        }

        reorderImages(post);
    }

    private void reorderImages(RecruitmentPost post) {
        List<RecruitmentPostImage> sorted = post.getImages().stream()
                .sorted(Comparator.comparingInt(RecruitmentPostImage::getSortOrder))
                .collect(Collectors.toList());
        for (int i = 0; i < sorted.size(); i++) {
            sorted.get(i).setSortOrder(i);
        }
    }

    private void syncThumbnailUrl(RecruitmentPost post) {
        if (post.getImages().isEmpty()) {
            post.setThumbnailUrl(null);
            return;
        }
        RecruitmentPostImage first = post.getImages().stream()
                .min(Comparator.comparingInt(RecruitmentPostImage::getSortOrder))
                .orElse(post.getImages().get(0));
        post.setThumbnailUrl(fileStorage.toPublicUrl(first.getStoredFileName()));
    }

    private void deleteAllImageFiles(RecruitmentPost post) {
        for (RecruitmentPostImage image : post.getImages()) {
            fileStorage.delete(image.getStoredFileName());
        }
    }

    private RecruitmentPostDetailResponse toDetailResponse(RecruitmentPost post, long commentCount) {
        return RecruitmentPostDetailResponse.from(post, fileStorage, commentCount);
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "사용자를 찾을 수 없습니다."));
    }

    private void validateParticipantLimit(int participantLimit) {
        if (participantLimit < 1) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "모집 인원은 1명 이상이어야 합니다.");
        }
    }
}

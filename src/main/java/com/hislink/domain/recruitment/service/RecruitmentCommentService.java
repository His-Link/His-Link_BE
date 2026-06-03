package com.hislink.domain.recruitment.service;

import com.hislink.common.exception.BusinessException;
import com.hislink.common.exception.ErrorCode;
import com.hislink.common.security.AuthorValidator;
import com.hislink.domain.auth.security.AuthenticatedUser;
import com.hislink.domain.recruitment.dto.RecruitmentCommentResponse;
import com.hislink.domain.recruitment.entity.RecruitmentComment;
import com.hislink.domain.recruitment.entity.RecruitmentPost;
import com.hislink.domain.recruitment.entity.RecruitmentStatus;
import com.hislink.domain.recruitment.repository.RecruitmentCommentRepository;
import com.hislink.domain.user.entity.User;
import com.hislink.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecruitmentCommentService {

    private final RecruitmentCommentRepository recruitmentCommentRepository;
    private final RecruitmentPostService recruitmentPostService;
    private final UserRepository userRepository;
    private final AuthorValidator authorValidator;

    @Transactional(readOnly = true)
    public List<RecruitmentCommentResponse> findByPostId(Long postId) {
        recruitmentPostService.getPostWithDetails(postId);
        return recruitmentCommentRepository.findByPostIdWithAuthor(postId).stream()
                .map(RecruitmentCommentResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public RecruitmentCommentResponse create(
            Long postId,
            String content,
            boolean application,
            AuthenticatedUser user
    ) {
        authorValidator.requireAuthenticated(user);
        RecruitmentPost post = recruitmentPostService.getPostWithDetails(postId);
        User author = getUser(user.getUserId());

        if (application) {
            validateApplication(post, author.getId());
        }

        RecruitmentComment comment = recruitmentCommentRepository.save(RecruitmentComment.builder()
                .post(post)
                .author(author)
                .content(content)
                .application(application)
                .build());

        if (application) {
            post.increaseCurrentCount();
            if (post.isFull()) {
                post.update(
                        post.getTitle(),
                        post.getDescription(),
                        post.getActivityType(),
                        post.getRecruitmentRole(),
                        RecruitmentStatus.CLOSED,
                        post.getParticipantLimit(),
                        post.getDeadline(),
                        post.getContactMethod()
                );
            }
        }

        return RecruitmentCommentResponse.from(comment);
    }

    @Transactional
    public RecruitmentCommentResponse update(Long commentId, String content, AuthenticatedUser user) {
        RecruitmentComment comment = getComment(commentId);
        authorValidator.validateAuthor(comment.getAuthor().getId(), user);
        comment.updateContent(content);
        return RecruitmentCommentResponse.from(comment);
    }

    @Transactional
    public void delete(Long commentId, AuthenticatedUser user) {
        RecruitmentComment comment = getComment(commentId);
        authorValidator.validateAuthor(comment.getAuthor().getId(), user);

        if (comment.isApplication()) {
            comment.getPost().decreaseCurrentCount();
            if (comment.getPost().getStatus() == RecruitmentStatus.CLOSED
                    && !comment.getPost().isFull()) {
                comment.getPost().update(
                        comment.getPost().getTitle(),
                        comment.getPost().getDescription(),
                        comment.getPost().getActivityType(),
                        comment.getPost().getRecruitmentRole(),
                        RecruitmentStatus.OPEN,
                        comment.getPost().getParticipantLimit(),
                        comment.getPost().getDeadline(),
                        comment.getPost().getContactMethod()
                );
            }
        }

        recruitmentCommentRepository.delete(comment);
    }

    private void validateApplication(RecruitmentPost post, Long userId) {
        if (post.getStatus() != RecruitmentStatus.OPEN) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "마감된 모집글에는 지원할 수 없습니다.");
        }
        if (post.getAuthor().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "본인이 작성한 모집글에는 지원할 수 없습니다.");
        }
        if (post.isFull()) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "모집 인원이 가득 찼습니다.");
        }
        if (recruitmentCommentRepository.existsApplicationByPostIdAndAuthorId(post.getId(), userId)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "이미 지원한 모집글입니다.");
        }
    }

    private RecruitmentComment getComment(Long commentId) {
        return recruitmentCommentRepository.findByIdWithAuthor(commentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "댓글을 찾을 수 없습니다."));
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "사용자를 찾을 수 없습니다."));
    }
}

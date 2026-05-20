package com.hislink.domain.community.service;

import com.hislink.common.exception.BusinessException;
import com.hislink.common.exception.ErrorCode;
import com.hislink.common.security.AuthorValidator;
import com.hislink.domain.auth.security.AuthenticatedUser;
import com.hislink.domain.community.dto.CommentResponse;
import com.hislink.domain.community.entity.Comment;
import com.hislink.domain.community.entity.CommunityPost;
import com.hislink.domain.community.repository.CommentRepository;
import com.hislink.domain.user.entity.User;
import com.hislink.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommunityPostService communityPostService;
    private final UserRepository userRepository;
    private final AuthorValidator authorValidator;

    @Transactional(readOnly = true)
    public List<CommentResponse> findByPostId(Long postId) {
        communityPostService.getPostWithAuthor(postId);
        return commentRepository.findByPostIdWithAuthor(postId).stream()
                .map(CommentResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public CommentResponse create(Long postId, String content, AuthenticatedUser user) {
        authorValidator.requireAuthenticated(user);
        CommunityPost post = communityPostService.getPostWithAuthor(postId);
        User author = getUser(user.getUserId());

        Comment comment = commentRepository.save(Comment.builder()
                .post(post)
                .author(author)
                .content(content)
                .build());

        return CommentResponse.from(comment);
    }

    @Transactional
    public CommentResponse update(Long commentId, String content, AuthenticatedUser user) {
        Comment comment = getComment(commentId);
        authorValidator.validateAuthor(comment.getAuthor().getId(), user);
        comment.updateContent(content);
        return CommentResponse.from(comment);
    }

    @Transactional
    public void delete(Long commentId, AuthenticatedUser user) {
        Comment comment = getComment(commentId);
        authorValidator.validateAuthor(comment.getAuthor().getId(), user);
        commentRepository.delete(comment);
    }

    private Comment getComment(Long commentId) {
        return commentRepository.findByIdWithAuthor(commentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "댓글을 찾을 수 없습니다."));
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "사용자를 찾을 수 없습니다."));
    }
}

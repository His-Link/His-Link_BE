package com.hislink.domain.community.service;

import com.hislink.common.exception.BusinessException;
import com.hislink.common.exception.ErrorCode;
import com.hislink.common.response.PageResponse;
import com.hislink.common.util.PageableSupport;
import com.hislink.common.security.AuthorValidator;
import com.hislink.domain.auth.security.AuthenticatedUser;
import com.hislink.domain.community.dto.*;
import com.hislink.domain.community.entity.CommunityPost;
import com.hislink.domain.community.entity.PostCategory;
import com.hislink.domain.community.entity.PostLike;
import com.hislink.domain.community.repository.CommentRepository;
import com.hislink.domain.community.repository.CommunityPostRepository;
import com.hislink.domain.community.repository.PostLikeRepository;
import com.hislink.domain.user.entity.User;
import com.hislink.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Set;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommunityPostService {

    private static final int MAX_PAGE_SIZE = 50;
    private static final Set<String> ALLOWED_SORT_PROPERTIES = Set.of(
            "createdAt", "viewCount", "likeCount", "title"
    );

    private final CommunityPostRepository communityPostRepository;
    private final CommentRepository commentRepository;
    private final PostLikeRepository postLikeRepository;
    private final UserRepository userRepository;
    private final AuthorValidator authorValidator;

    @Transactional
    public CommunityPostDetailResponse create(
            PostCategory category,
            String title,
            String content,
            AuthenticatedUser user
    ) {
        authorValidator.requireAuthenticated(user);
        User author = getUser(user.getUserId());

        CommunityPost post = communityPostRepository.save(CommunityPost.builder()
                .author(author)
                .category(category)
                .title(title)
                .content(content)
                .build());

        return CommunityPostDetailResponse.from(post, false, 0);
    }

    @Transactional(readOnly = true)
    public PageResponse<CommunityPostSummaryResponse> findAll(PostCategory category, Pageable pageable) {
        Pageable safePageable = clampPageable(pageable);
        Page<CommunityPost> page = category == null
                ? communityPostRepository.findAllWithAuthor(safePageable)
                : communityPostRepository.findByCategoryWithAuthor(category, safePageable);

        return PageResponse.from(page.map(post -> CommunityPostSummaryResponse.from(
                post,
                commentRepository.countByPostId(post.getId())
        )));
    }

    @Transactional
    public CommunityPostDetailResponse findById(Long postId, AuthenticatedUser user) {
        CommunityPost post = getPostWithAuthor(postId);
        post.increaseViewCount();

        boolean likedByMe = user != null
                && postLikeRepository.existsByPostIdAndUserId(postId, user.getUserId());
        long commentCount = commentRepository.countByPostId(postId);

        return CommunityPostDetailResponse.from(post, likedByMe, commentCount);
    }

    @Transactional
    public CommunityPostDetailResponse update(
            Long postId,
            PostCategory category,
            String title,
            String content,
            AuthenticatedUser user
    ) {
        CommunityPost post = getPostWithAuthor(postId);
        authorValidator.validateAuthor(post.getAuthor().getId(), user);
        post.update(category, title, content);

        boolean likedByMe = postLikeRepository.existsByPostIdAndUserId(postId, user.getUserId());
        return CommunityPostDetailResponse.from(post, likedByMe, commentRepository.countByPostId(postId));
    }

    @Transactional
    public void delete(Long postId, AuthenticatedUser user) {
        CommunityPost post = getPostWithAuthor(postId);
        authorValidator.validateAuthor(post.getAuthor().getId(), user);
        communityPostRepository.delete(post);
    }

    @Transactional
    public LikeToggleResponse toggleLike(Long postId, AuthenticatedUser user) {
        authorValidator.requireAuthenticated(user);
        CommunityPost post = getPostWithAuthor(postId);

        return postLikeRepository.findByPostIdAndUserId(postId, user.getUserId())
                .map(existing -> {
                    postLikeRepository.delete(existing);
                    post.decreaseLikeCount();
                    return new LikeToggleResponse(false, post.getLikeCount());
                })
                .orElseGet(() -> {
                    User liker = getUser(user.getUserId());
                    postLikeRepository.save(PostLike.builder().post(post).user(liker).build());
                    post.increaseLikeCount();
                    return new LikeToggleResponse(true, post.getLikeCount());
                });
    }

    public CommunityPost getPostWithAuthor(Long postId) {
        return communityPostRepository.findByIdWithAuthor(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "게시글을 찾을 수 없습니다."));
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "사용자를 찾을 수 없습니다."));
    }

    private Pageable clampPageable(Pageable pageable) {
        return PageableSupport.sanitize(
                pageable,
                MAX_PAGE_SIZE,
                ALLOWED_SORT_PROPERTIES,
                "createdAt",
                Sort.Direction.DESC
        );
    }
}

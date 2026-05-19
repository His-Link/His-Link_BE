package com.hislink.domain.community.dto;

import com.hislink.domain.community.entity.CommunityPost;
import com.hislink.domain.community.entity.PostCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Schema(description = "커뮤니티 게시글 상세")
public class CommunityPostDetailResponse {

    private final Long id;
    private final PostCategory category;
    private final String title;
    private final String content;
    private final AuthorSummaryResponse author;
    private final int viewCount;
    private final int likeCount;
    private final boolean likedByMe;
    private final long commentCount;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public static CommunityPostDetailResponse from(CommunityPost post, boolean likedByMe, long commentCount) {
        return new CommunityPostDetailResponse(
                post.getId(),
                post.getCategory(),
                post.getTitle(),
                post.getContent(),
                AuthorSummaryResponse.from(post.getAuthor()),
                post.getViewCount(),
                post.getLikeCount(),
                likedByMe,
                commentCount,
                post.getCreatedAt(),
                post.getUpdatedAt()
        );
    }
}

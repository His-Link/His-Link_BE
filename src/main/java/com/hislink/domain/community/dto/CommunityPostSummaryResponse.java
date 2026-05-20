package com.hislink.domain.community.dto;

import com.hislink.domain.community.entity.CommunityPost;
import com.hislink.domain.community.entity.PostCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Schema(description = "커뮤니티 게시글 목록 항목")
public class CommunityPostSummaryResponse {

    @Schema(description = "게시글 ID", example = "1")
    private final Long id;

    @Schema(description = "카테고리")
    private final PostCategory category;

    @Schema(description = "제목")
    private final String title;

    @Schema(description = "본문 미리보기 (최대 120자)")
    private final String contentPreview;

    @Schema(description = "작성자")
    private final AuthorSummaryResponse author;

    @Schema(description = "조회수")
    private final int viewCount;

    @Schema(description = "좋아요 수")
    private final int likeCount;

    @Schema(description = "댓글 수")
    private final long commentCount;

    @Schema(description = "작성 일시")
    private final LocalDateTime createdAt;

    public static CommunityPostSummaryResponse from(CommunityPost post, long commentCount) {
        return new CommunityPostSummaryResponse(
                post.getId(),
                post.getCategory(),
                post.getTitle(),
                toPreview(post.getContent()),
                AuthorSummaryResponse.from(post.getAuthor()),
                post.getViewCount(),
                post.getLikeCount(),
                commentCount,
                post.getCreatedAt()
        );
    }

    private static String toPreview(String content) {
        if (content == null) {
            return "";
        }
        if (content.length() <= 120) {
            return content;
        }
        return content.substring(0, 120) + "...";
    }
}

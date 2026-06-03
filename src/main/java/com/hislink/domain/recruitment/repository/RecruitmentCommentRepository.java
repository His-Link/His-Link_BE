package com.hislink.domain.recruitment.repository;

import com.hislink.domain.recruitment.entity.RecruitmentComment;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RecruitmentCommentRepository extends JpaRepository<RecruitmentComment, Long> {

    @Query("SELECT COUNT(c) FROM RecruitmentComment c WHERE c.post.id = :postId")
    long countByPostId(@Param("postId") Long postId);

    @Query(
            "SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END "
                    + "FROM RecruitmentComment c "
                    + "WHERE c.post.id = :postId AND c.author.id = :authorId AND c.application = true"
    )
    boolean existsApplicationByPostIdAndAuthorId(
            @Param("postId") Long postId,
            @Param("authorId") Long authorId
    );

    @EntityGraph(attributePaths = {"author", "post"})
    @Query("SELECT c FROM RecruitmentComment c WHERE c.post.id = :postId ORDER BY c.createdAt ASC")
    List<RecruitmentComment> findByPostIdWithAuthor(@Param("postId") Long postId);

    @EntityGraph(attributePaths = {"author", "post"})
    @Query("SELECT c FROM RecruitmentComment c WHERE c.id = :id")
    Optional<RecruitmentComment> findByIdWithAuthor(@Param("id") Long id);
}

package com.hislink.domain.community.repository;

import com.hislink.domain.community.entity.Comment;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    long countByPostId(Long postId);

    @EntityGraph(attributePaths = {"author", "post"})
    @Query("SELECT c FROM Comment c WHERE c.post.id = :postId ORDER BY c.createdAt ASC")
    List<Comment> findByPostIdWithAuthor(@Param("postId") Long postId);

    @EntityGraph(attributePaths = {"author", "post"})
    @Query("SELECT c FROM Comment c WHERE c.id = :id")
    Optional<Comment> findByIdWithAuthor(@Param("id") Long id);
}

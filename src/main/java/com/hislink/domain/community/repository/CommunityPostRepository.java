package com.hislink.domain.community.repository;

import com.hislink.domain.community.entity.CommunityPost;
import com.hislink.domain.community.entity.PostCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CommunityPostRepository extends JpaRepository<CommunityPost, Long> {

    Page<CommunityPost> findByCategory(PostCategory category, Pageable pageable);

    @EntityGraph(attributePaths = "author")
    @Query("SELECT p FROM CommunityPost p WHERE p.id = :id")
    Optional<CommunityPost> findByIdWithAuthor(@Param("id") Long id);

    @EntityGraph(attributePaths = "author")
    @Query("SELECT p FROM CommunityPost p")
    Page<CommunityPost> findAllWithAuthor(Pageable pageable);

    @EntityGraph(attributePaths = "author")
    @Query("SELECT p FROM CommunityPost p WHERE p.category = :category")
    Page<CommunityPost> findByCategoryWithAuthor(@Param("category") PostCategory category, Pageable pageable);
}

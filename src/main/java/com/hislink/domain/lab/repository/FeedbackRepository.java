package com.hislink.domain.lab.repository;

import com.hislink.domain.lab.entity.Feedback;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    @EntityGraph(attributePaths = "author")
    @Query("SELECT f FROM Feedback f WHERE f.project.id = :projectId ORDER BY f.createdAt DESC")
    List<Feedback> findByProjectIdWithAuthor(@Param("projectId") Long projectId);

    @EntityGraph(attributePaths = {"author", "project"})
    @Query("SELECT f FROM Feedback f WHERE f.id = :id")
    Optional<Feedback> findByIdWithAuthor(@Param("id") Long id);

    boolean existsByProjectIdAndAuthorId(Long projectId, Long authorId);

    List<Feedback> findByProjectId(Long projectId);
}

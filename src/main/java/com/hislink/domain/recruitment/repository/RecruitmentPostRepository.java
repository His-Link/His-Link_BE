package com.hislink.domain.recruitment.repository;

import com.hislink.domain.recruitment.entity.RecruitmentPost;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RecruitmentPostRepository
        extends JpaRepository<RecruitmentPost, Long>, JpaSpecificationExecutor<RecruitmentPost> {

    @EntityGraph(attributePaths = {"author", "techStacks", "images"})
    @Query("SELECT p FROM RecruitmentPost p WHERE p.id = :id")
    Optional<RecruitmentPost> findByIdWithDetails(@Param("id") Long id);
}

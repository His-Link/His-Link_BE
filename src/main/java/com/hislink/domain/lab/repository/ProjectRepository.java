package com.hislink.domain.lab.repository;

import com.hislink.domain.lab.entity.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long>, JpaSpecificationExecutor<Project> {

    @EntityGraph(attributePaths = {"author", "techStacks", "images"})
    @Query("SELECT p FROM Project p WHERE p.id = :id")
    Optional<Project> findByIdWithDetails(@Param("id") Long id);

    @EntityGraph(attributePaths = "author")
    @Query("SELECT p FROM Project p")
    Page<Project> findAllWithAuthor(Pageable pageable);
}

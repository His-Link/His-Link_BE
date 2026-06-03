package com.hislink.domain.techstack.repository;

import com.hislink.domain.techstack.entity.TechStack;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface TechStackRepository extends JpaRepository<TechStack, Long> {

    Optional<TechStack> findByNameIgnoreCase(String name);

    List<TechStack> findAllByOrderByNameAsc();

    List<TechStack> findByIdIn(Collection<Long> ids);

    boolean existsByNameIgnoreCase(String name);
}

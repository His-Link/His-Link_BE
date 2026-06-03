package com.hislink.domain.lab.repository;

import com.hislink.domain.lab.entity.Project;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;

public final class ProjectSpecifications {

    private ProjectSpecifications() {
    }

    public static Specification<Project> keywordContains(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return null;
        }
        String pattern = "%" + keyword.trim().toLowerCase() + "%";
        return (root, query, cb) -> cb.or(
                cb.like(cb.lower(root.get("title")), pattern),
                cb.like(cb.lower(root.get("summary")), pattern),
                cb.like(cb.lower(root.get("testRequest")), pattern)
        );
    }

    public static Specification<Project> hasTechStackName(String techStack) {
        if (techStack == null || techStack.isBlank()) {
            return null;
        }
        return (root, query, cb) -> {
            query.distinct(true);
            Join<Object, Object> stacks = root.join("techStacks", JoinType.INNER);
            return cb.equal(cb.lower(stacks.get("name")), techStack.trim().toLowerCase());
        };
    }

    public static Specification<Project> fetchAuthor() {
        return (root, query, cb) -> {
            if (!isCountQuery(query)) {
                root.fetch("author", JoinType.INNER);
                query.distinct(true);
            }
            return cb.conjunction();
        };
    }

    private static boolean isCountQuery(javax.persistence.criteria.CriteriaQuery<?> query) {
        return query.getResultType() == Long.class || query.getResultType() == long.class;
    }
}

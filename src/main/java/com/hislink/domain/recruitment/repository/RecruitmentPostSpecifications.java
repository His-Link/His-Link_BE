package com.hislink.domain.recruitment.repository;

import com.hislink.domain.recruitment.entity.RecruitmentActivityType;
import com.hislink.domain.recruitment.entity.RecruitmentPost;
import com.hislink.domain.recruitment.entity.RecruitmentRole;
import com.hislink.domain.recruitment.entity.RecruitmentStatus;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.JoinType;

public final class RecruitmentPostSpecifications {

    private RecruitmentPostSpecifications() {
    }

    public static Specification<RecruitmentPost> hasActivityType(RecruitmentActivityType activityType) {
        return (root, query, cb) ->
                activityType == null ? null : cb.equal(root.get("activityType"), activityType);
    }

    public static Specification<RecruitmentPost> hasRole(RecruitmentRole role) {
        return (root, query, cb) ->
                role == null ? null : cb.equal(root.get("recruitmentRole"), role);
    }

    public static Specification<RecruitmentPost> hasStatus(RecruitmentStatus status) {
        return (root, query, cb) ->
                status == null ? null : cb.equal(root.get("status"), status);
    }

    public static Specification<RecruitmentPost> withAuthor() {
        return (root, query, cb) -> {
            root.fetch("author", JoinType.INNER);
            query.distinct(true);
            return null;
        };
    }

    public static Specification<RecruitmentPost> hasTechStackName(String techStack) {
        return (root, query, cb) -> {
            if (techStack == null || techStack.isBlank()) {
                return null;
            }
            query.distinct(true);
            return cb.equal(
                    cb.lower(root.join("techStacks", JoinType.INNER).get("name")),
                    techStack.trim().toLowerCase()
            );
        };
    }
}

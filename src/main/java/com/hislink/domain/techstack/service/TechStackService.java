package com.hislink.domain.techstack.service;

import com.hislink.common.exception.BusinessException;
import com.hislink.common.exception.ErrorCode;
import com.hislink.common.security.AuthorValidator;
import com.hislink.domain.auth.security.AuthenticatedUser;
import com.hislink.domain.techstack.dto.TechStackResponse;
import com.hislink.domain.techstack.entity.TechStack;
import com.hislink.domain.techstack.repository.TechStackRepository;
import com.hislink.domain.user.entity.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TechStackService {

    private final TechStackRepository techStackRepository;
    private final AuthorValidator authorValidator;

    @Transactional(readOnly = true)
    public List<TechStackResponse> findAll() {
        return techStackRepository.findAllByOrderByNameAsc().stream()
                .map(TechStackResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public TechStackResponse create(String name, AuthenticatedUser user) {
        authorValidator.requireAuthenticated(user);
        if (user.getRole() != Role.ADMIN) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
        String trimmed = name.trim();
        if (techStackRepository.existsByNameIgnoreCase(trimmed)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "이미 등록된 기술 스택입니다.");
        }
        TechStack saved = techStackRepository.save(TechStack.builder().name(trimmed).build());
        return TechStackResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public Set<TechStack> resolveTechStacks(List<Long> techStackIds) {
        if (techStackIds == null || techStackIds.isEmpty()) {
            return Set.of();
        }
        List<Long> distinctIds = techStackIds.stream().distinct().collect(Collectors.toList());
        List<TechStack> found = techStackRepository.findByIdIn(distinctIds);
        if (found.size() != distinctIds.size()) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "존재하지 않는 기술 스택 ID가 포함되어 있습니다.");
        }
        return new HashSet<>(found);
    }
}

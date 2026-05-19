package com.hislink.common.security;

import com.hislink.common.exception.BusinessException;
import com.hislink.common.exception.ErrorCode;
import com.hislink.domain.auth.security.AuthenticatedUser;
import com.hislink.domain.user.entity.Role;
import org.springframework.stereotype.Component;

@Component
public class AuthorValidator {

    public void requireAuthenticated(AuthenticatedUser user) {
        if (user == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
    }

    public void validateAuthor(Long authorId, AuthenticatedUser user) {
        requireAuthenticated(user);
        if (user.getRole() == Role.ADMIN) {
            return;
        }
        if (!user.getUserId().equals(authorId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
    }
}

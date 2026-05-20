package com.hislink.common.security;

import com.hislink.common.exception.BusinessException;
import com.hislink.common.exception.ErrorCode;
import com.hislink.domain.auth.security.AuthenticatedUser;
import com.hislink.domain.user.entity.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AuthorValidatorTest {

    private final AuthorValidator authorValidator = new AuthorValidator();

    @Test
    @DisplayName("작성자 본인이면 통과한다")
    void validateAuthor_sameUser() {
        AuthenticatedUser user = new AuthenticatedUser(1L, "a@handong.ac.kr", Role.USER);

        assertThatCode(() -> authorValidator.validateAuthor(1L, user))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("ADMIN은 타인 리소스도 수정할 수 있다")
    void validateAuthor_admin() {
        AuthenticatedUser admin = new AuthenticatedUser(99L, "admin@handong.ac.kr", Role.ADMIN);

        assertThatCode(() -> authorValidator.validateAuthor(1L, admin))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("타인 리소스는 FORBIDDEN이다")
    void validateAuthor_forbidden() {
        AuthenticatedUser user = new AuthenticatedUser(2L, "b@handong.ac.kr", Role.USER);

        assertThatThrownBy(() -> authorValidator.validateAuthor(1L, user))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> {
                    BusinessException businessException = (BusinessException) ex;
                    assert businessException.getErrorCode() == ErrorCode.FORBIDDEN;
                });
    }

    @Test
    @DisplayName("비로그인은 UNAUTHORIZED이다")
    void requireAuthenticated_null() {
        assertThatThrownBy(() -> authorValidator.requireAuthenticated(null))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> {
                    BusinessException businessException = (BusinessException) ex;
                    assert businessException.getErrorCode() == ErrorCode.UNAUTHORIZED;
                });
    }
}

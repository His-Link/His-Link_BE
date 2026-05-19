package com.hislink.domain.auth.security;

import com.hislink.domain.user.entity.Role;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.Collections;

@Hidden
@Getter
public class AuthenticatedUser {

    private final Long userId;
    private final String email;
    private final Role role;

    public AuthenticatedUser(Long userId, String email, Role role) {
        this.userId = userId;
        this.email = email;
        this.role = role;
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }
}

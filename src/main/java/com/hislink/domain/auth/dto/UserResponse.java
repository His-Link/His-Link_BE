package com.hislink.domain.auth.dto;

import com.hislink.domain.user.entity.Role;
import com.hislink.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserResponse {

    private final Long id;
    private final String email;
    private final String name;
    private final String profileImageUrl;
    private final Role role;

    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getProfileImageUrl(),
                user.getRole()
        );
    }
}

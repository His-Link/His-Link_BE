package com.hislink.common.security;

import com.hislink.config.DevAuthProperties;
import com.hislink.domain.auth.security.AuthenticatedUser;
import com.hislink.domain.user.entity.Role;
import com.hislink.domain.user.entity.User;
import com.hislink.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class DevUserResolver {

    private static final String DEV_GOOGLE_SUB_PREFIX = "dev-local-";

    private final DevAuthProperties devAuthProperties;
    private final UserRepository userRepository;

    @Transactional
    public AuthenticatedUser resolveDefaultUser() {
        String email = devAuthProperties.getDefaultUserEmail();
        User user = userRepository.findByEmail(email)
                .orElseGet(() -> userRepository.save(User.builder()
                        .email(email)
                        .name("Local Dev User")
                        .profileImageUrl(null)
                        .googleSub(DEV_GOOGLE_SUB_PREFIX + email)
                        .role(Role.USER)
                        .build()));

        return new AuthenticatedUser(user.getId(), user.getEmail(), user.getRole());
    }
}

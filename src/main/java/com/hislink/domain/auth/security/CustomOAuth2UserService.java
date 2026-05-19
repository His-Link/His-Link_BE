package com.hislink.domain.auth.security;

import com.hislink.common.exception.AuthException;
import com.hislink.domain.auth.service.AuthService;
import com.hislink.domain.auth.validator.EmailDomainValidator;
import com.hislink.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final AuthService authService;
    private final EmailDomainValidator emailDomainValidator;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);

        String email = oauth2User.getAttribute("email");
        String googleSub = oauth2User.getAttribute("sub");
        String name = oauth2User.getAttribute("name");
        String picture = oauth2User.getAttribute("picture");

        if (!StringUtils.hasText(email) || !StringUtils.hasText(googleSub)) {
            throw new OAuth2AuthenticationException(
                    new OAuth2Error("invalid_user"),
                    "Google account email information is missing."
            );
        }

        if (!emailDomainValidator.isAllowed(email)) {
            throw new OAuth2AuthenticationException(
                    new OAuth2Error("invalid_domain"),
                    "Only Handong University email domains are allowed."
            );
        }

        try {
            User user = authService.upsertGoogleUser(email, name, picture, googleSub);
            return new CustomOAuth2User(user, oauth2User.getAttributes());
        } catch (AuthException ex) {
            throw new OAuth2AuthenticationException(new OAuth2Error("invalid_domain"), ex.getMessage());
        }
    }
}

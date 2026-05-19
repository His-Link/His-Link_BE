package com.hislink.domain.auth.validator;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class EmailDomainValidator {

    private final Set<String> allowedDomains;

    public EmailDomainValidator(@Value("${app.auth.allowed-email-domains}") String allowedDomains) {
        this.allowedDomains = Arrays.stream(allowedDomains.split(","))
                .map(String::trim)
                .filter(StringUtils::hasText)
                .map(domain -> domain.toLowerCase(Locale.ROOT))
                .collect(Collectors.toSet());
    }

    public boolean isAllowed(String email) {
        if (!StringUtils.hasText(email) || !email.contains("@")) {
            return false;
        }
        String domain = email.substring(email.indexOf('@') + 1).toLowerCase(Locale.ROOT);
        return allowedDomains.contains(domain);
    }
}

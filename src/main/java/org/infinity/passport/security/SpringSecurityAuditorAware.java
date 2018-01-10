package org.infinity.passport.security;

import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import org.infinity.passport.config.ApplicationConstants;
import org.infinity.passport.utils.SecurityUtils;

/**
 * Implementation of AuditorAware based on Spring Security.
 */
@Component
public class SpringSecurityAuditorAware implements AuditorAware<String> {

    @Override
    public String getCurrentAuditor() {
        String userName = SecurityUtils.getCurrentUserName();
        return (userName != null ? userName : ApplicationConstants.SYSTEM_ACCOUNT);
    }
}

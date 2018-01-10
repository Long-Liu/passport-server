package org.infinity.passport.service;

import org.infinity.passport.domain.AppAuthority;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AppAuthorityService {

    AppAuthority insert(String appName, String authorityName);

    AppAuthority update(String id, String appName, String authorityName);

    Page<AppAuthority> findByAppNameAndAuthorityNameCombinations(Pageable pageable, String appName,
            String authorityName);

}
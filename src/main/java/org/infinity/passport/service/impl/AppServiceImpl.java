package org.infinity.passport.service.impl;

import java.util.Optional;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.infinity.passport.domain.App;
import org.infinity.passport.domain.AppAuthority;
import org.infinity.passport.repository.AppAuthorityRepository;
import org.infinity.passport.repository.AppRepository;
import org.infinity.passport.service.AppService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AppServiceImpl implements AppService {

    private static final Logger    LOGGER = LoggerFactory.getLogger(AppServiceImpl.class);

    @Autowired
    private AppRepository          appRepository;

    @Autowired
    private AppAuthorityRepository appAuthorityRepository;

    @Override
    public App insert(String name, Boolean enabled, Set<String> authorityNames) {
        App newApp = new App(name, enabled);
        appRepository.save(newApp);

        if (CollectionUtils.isNotEmpty(authorityNames)) {
            authorityNames.forEach(authorityName -> {
                appAuthorityRepository.insert(new AppAuthority(newApp.getName(), authorityName));
            });
        }

        LOGGER.debug("Created Information for app: {}", newApp);
        return newApp;
    }

    @Override
    public void update(String name, Boolean enabled, Set<String> authorityNames) {
        Optional.of(appRepository.findOne(name)).ifPresent(app -> {
            app.setEnabled(enabled);
            appRepository.save(app);
            LOGGER.debug("Updated app: {}", app);

            if (CollectionUtils.isNotEmpty(authorityNames)) {
                appAuthorityRepository.deleteByAppName(app.getName());
                authorityNames.forEach(authorityName -> {
                    appAuthorityRepository.insert(new AppAuthority(app.getName(), authorityName));
                });
                LOGGER.debug("Updated user authorities");
            } else {
                appAuthorityRepository.deleteByAppName(app.getName());
            }
        });
    }
}
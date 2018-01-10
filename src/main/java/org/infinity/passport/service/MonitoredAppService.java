package org.infinity.passport.service;

import org.infinity.passport.domain.MonitoredApp;

import java.util.List;

public interface MonitoredAppService {
    List<MonitoredApp> findAll();
}

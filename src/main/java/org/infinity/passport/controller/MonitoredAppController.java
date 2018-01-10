package org.infinity.passport.controller;

import org.infinity.passport.domain.MonitoredApp;
import org.infinity.passport.service.MonitoredAppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MonitoredAppController {

    private final MonitoredAppService monitoredAppService;

    @Autowired
    public MonitoredAppController(MonitoredAppService monitoredAppService) {
        this.monitoredAppService = monitoredAppService;
    }

    @GetMapping("api/monitoredApps")
    public List<MonitoredApp> getMonitoredApps() {
        return monitoredAppService.findAll();
    }
}
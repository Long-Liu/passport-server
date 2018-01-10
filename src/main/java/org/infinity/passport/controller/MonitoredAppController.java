package org.infinity.passport.controller;

import org.infinity.passport.domain.MonitoredApp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MonitoredAppController {

    private final MongoTemplate mongoTemplate;

    @Autowired
    public MonitoredAppController(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @GetMapping("api/monitoredApps")
    public List<MonitoredApp> getMonitoredApps() {
        return mongoTemplate.findAll(MonitoredApp.class);
    }
}

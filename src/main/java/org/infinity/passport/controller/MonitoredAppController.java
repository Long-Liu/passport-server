package org.infinity.passport.controller;

import org.infinity.passport.domain.MonitoredApp;
import org.infinity.passport.service.MonitoredAppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MonitoredAppController {

    private final MonitoredAppService monitoredAppService;

    private final MongoTemplate mongoTemplate;

    @Autowired
    public MonitoredAppController(MonitoredAppService monitoredAppService,
                                  MongoTemplate mongoTemplate) {
        this.monitoredAppService = monitoredAppService;
        this.mongoTemplate = mongoTemplate;
    }

    @GetMapping("api/monitoredApps")
    public List<MonitoredApp> getMonitoredApps() {
        return monitoredAppService.findAll();
    }

    @GetMapping("api/appConfig")
    public List<MonitoredApp> loadApps() {
        List<MonitoredApp> all = mongoTemplate.findAll(MonitoredApp.class);
        all.forEach(System.out::println);
        return mongoTemplate.findAll(MonitoredApp.class);
    }

    @GetMapping("api/appConfig/{appName}")
    public MonitoredApp loadApps(@PathVariable(value = "appName") String appName) {
        Query query=Query.query(Criteria.where("appName").is(appName));
        return mongoTemplate.findOne(query, MonitoredApp.class);
    }
}

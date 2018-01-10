package org.infinity.passport.service.impl;

import org.infinity.passport.domain.HealthState;
import org.infinity.passport.domain.MonitoredApp;
import org.infinity.passport.domain.Node;
import org.infinity.passport.service.MonitoredAppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class MonitoredAppServiceImpl implements MonitoredAppService {
    private final MongoTemplate mongoTemplate;

    private final RestTemplate restTemplate;

    @Autowired
    public MonitoredAppServiceImpl(MongoTemplate mongoTemplate, RestTemplate restTemplate) {
        this.mongoTemplate = mongoTemplate;
        this.restTemplate = restTemplate;
    }

    @Override
    public List<MonitoredApp> findAll() {
        List<MonitoredApp> all = mongoTemplate.findAll(MonitoredApp.class);
        AtomicInteger errorSize = new AtomicInteger(0);
        all.parallelStream().forEach(e -> {
            e.getNodes().parallelStream().forEach(o -> {
                String statusName = getNodeStatus(o).equals("up") ? "健康" : "出错";
                String statusColor = statusName.equals("健康") ? "蓝色" : "红色";
                HealthState healthState = new HealthState(statusName, statusColor);
                o.setHealthState(healthState);
                if (statusName.equals("出错")) {
                    errorSize.getAndIncrement();
                }
            });
            if (all.size() >>> 2 < errorSize.get()) {
                e.setHealthState(new HealthState("出错", "红色"));
            } else if (all.size() >>> 2 >= errorSize.get() && errorSize.get() >= 1) {
                e.setHealthState(new HealthState("警告", "橙色"));
            } else {
                e.setHealthState(new HealthState("正常", "蓝色"));
            }
        });
        return all;
    }

    private String getNodeStatus(Node node) {
        ResponseEntity<Map> entity = restTemplate.getForEntity(node.getServerAddress()
                + node.getPort() + node.getHealthContextPath(), Map.class);
        return ((String) entity.getBody().get("status"));
    }
}

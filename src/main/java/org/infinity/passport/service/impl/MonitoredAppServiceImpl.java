package org.infinity.passport.service.impl;

import com.esotericsoftware.kryo.util.ObjectMap;
import org.infinity.passport.domain.HealthState;
import org.infinity.passport.domain.MonitoredApp;
import org.infinity.passport.domain.Node;
import org.infinity.passport.service.MonitoredAppService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger LOGGER = LoggerFactory.getLogger(MonitoredAppServiceImpl.class);

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
        all.forEach(e -> {
            AtomicInteger errorSize = new AtomicInteger(0);
            e.getNodes().forEach(o -> {
                try {
                    String statusName = getNodeStatus(o).substring(1,14).equals("\"status\":\"UP\"") ? "健康" : "出错";
                    String statusColor = statusName.equals("健康") ? "蓝色" : "红色";
                    HealthState healthState = new HealthState(statusName, statusColor);
                    o.setHealthState(healthState);
                    if (statusName.equals("出错")) {
                        errorSize.getAndIncrement();
                    }
                } catch (Exception e1) {
                    errorSize.getAndIncrement();
                }
            });
            int i = errorSize.get();
            if (all.size() >>> 1 < i) {
                e.setHealthState(new HealthState("出错", "红色"));
            } else if (all.size() >>> 1 >= i && i >= 1) {
                e.setHealthState(new HealthState("警告", "橙色"));
            } else {
                e.setHealthState(new HealthState("健康", "蓝色"));
            }
        });
        return all;
    }

    private String getNodeStatus(Node node) {
        ResponseEntity<String> entity = restTemplate.getForEntity("http://" + node.getServerAddress() + ":"
                + node.getPort() + node.getHealthContextPath(), String.class);
        return entity.getBody();
    }
}

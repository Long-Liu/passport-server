package org.infinity.passport.service.impl;

import org.infinity.passport.domain.MonitoredApp;
import org.infinity.passport.domain.Node;
import org.infinity.passport.service.MailService;
import org.infinity.passport.service.MonitoredAppService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class MonitoredAppServiceImpl implements MonitoredAppService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MonitoredAppServiceImpl.class);

    private final MongoTemplate mongoTemplate;

    private final RestTemplate restTemplate;

    private final MailService mailService;

    @Autowired
    public MonitoredAppServiceImpl(MongoTemplate mongoTemplate, RestTemplate restTemplate,
                                   MailService mailService) {
        this.mongoTemplate = mongoTemplate;
        this.restTemplate = restTemplate;
        this.mailService = mailService;
    }

    @Override
    public List<MonitoredApp> findAll() {
        List<MonitoredApp> all = mongoTemplate.findAll(MonitoredApp.class);
        all.forEach(e -> {
            AtomicInteger errorSize = new AtomicInteger(0);
            e.getNodes().forEach(o -> {
                try {
                    String healthState = getNodeStatus(o).substring(1, 14).equals("\"status\":\"UP\"") ? "健康" : "出错";
                    o.setHealthState(healthState);
                    if (healthState.equals("出错")) {
                        errorSize.getAndIncrement();
                    }
                } catch (Exception e1) {
                    errorSize.getAndIncrement();
                    o.setHealthState("出错");
                }
            });
            int i = errorSize.get();
            if (all.size() >>> 1 < i) {
                e.setHealthState("出错");
            } else if (all.size() >>> 1 >= i && i >= 1) {
                e.setHealthState("警告");
            } else {
                e.setHealthState("健康");
            }
        });
        return all;
    }

    @Override
    public List<MonitoredApp> findAllWithoutQuest() {
        return mongoTemplate.findAll(MonitoredApp.class);
    }

    private String getNodeStatus(Node node) {
        ResponseEntity<String> entity = restTemplate.getForEntity("http://" + node.getServerAddress() + ":"
                + node.getPort() + node.getHealthContextPath(), String.class);
        return entity.getBody();
    }

//    @Scheduled(cron = "*/60 * * * * *")
    public void sendMailIfNodeDisable() {
        List<MonitoredApp> apps = mongoTemplate.findAll(MonitoredApp.class);
        apps.forEach(e -> {
            AtomicInteger integer = new AtomicInteger(0);
            StringBuilder msg = new StringBuilder("节点：");
            List<Node> nodes = e.getNodes();
            nodes.forEach(o -> {
                String nodeStatus;
                try {
                    nodeStatus = getNodeStatus(o);
                    if (!nodeStatus.equalsIgnoreCase("\"status\":\"UP\"")) {
                        integer.getAndIncrement();
                        msg.append(o.getServerAddress()).append(',');
                    }
                } catch (Exception e1) {
                    LOGGER.warn(e1.getMessage());
                    integer.getAndIncrement();
                }
            });
            if (integer.get() > 0) {
                String content = msg.substring(0, msg.length() - 1).concat("不可用");
                mailService.sendEmail(e.getResponsiblePerson().getEmail(), "监控消息",
                        content, false, false);
                LOGGER.warn(content);
            }
        });
    }
}

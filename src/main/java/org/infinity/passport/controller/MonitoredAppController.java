package org.infinity.passport.controller;

import org.apache.commons.collections.MapUtils;
import org.infinity.passport.domain.MonitoredApp;
import org.infinity.passport.domain.Node;
import org.infinity.passport.domain.ResponsiblePerson;
import org.infinity.passport.repository.MonitoredAppRepository;
import org.infinity.passport.service.MonitoredAppService;
import org.infinity.passport.utils.PaginationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.net.URISyntaxException;
import java.util.*;

@RestController
public class MonitoredAppController {

    private final MonitoredAppService monitoredAppService;

    private final MongoTemplate mongoTemplate;

    private final MonitoredAppRepository monitoredAppRepository;

    @Autowired
    public MonitoredAppController(MonitoredAppService monitoredAppService,
                                  MongoTemplate mongoTemplate,
                                  MonitoredAppRepository monitoredAppRepository) {
        this.monitoredAppService = monitoredAppService;
        this.mongoTemplate = mongoTemplate;
        this.monitoredAppRepository = monitoredAppRepository;
    }

    @GetMapping("api/monitoredApps")
    public List<MonitoredApp> getMonitoredApps() {
        return monitoredAppService.findAll();
    }

    @GetMapping("api/appConfig")
    public ResponseEntity<List<MonitoredApp>> loadApps(Pageable pageable) throws URISyntaxException {
        Page<MonitoredApp> monitoredAppPage = monitoredAppRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtils.generatePaginationHttpHeaders(monitoredAppPage, "api/appConfig");
        return new ResponseEntity<>(monitoredAppPage.getContent(), headers, HttpStatus.OK);
    }

    @GetMapping("api/appConfig/{appName}")
    public MonitoredApp loadApps(@PathVariable(value = "appName") String appName) {
        Query query = Query.query(Criteria.where("appName").is(appName));
        return mongoTemplate.findOne(query, MonitoredApp.class);
    }

    @PostMapping("api/appConfig")
    public void updateApps(@RequestBody Map<String, Object> map) {
        if (Objects.nonNull(map)) {
            Query query = Query.query(Criteria.where("appName").is(map.get("appName")));
            MonitoredApp app = mongoTemplate.findOne(query, MonitoredApp.class);
            if (Objects.nonNull(app)) {
                mongoTemplate.remove(query, MonitoredApp.class);
                mongoTemplate.insert(map.get("monitoredApp"), "MonitoredApp");
            }
        }
    }

    @DeleteMapping("api/appConfig/{appName}")
    public void removeOne(@PathVariable String appName) {
        Query query = Query.query(Criteria.where("appName").is(appName));
        mongoTemplate.remove(query, MonitoredApp.class);
    }

    @PutMapping("api/appConfig")
    public void createOne(@RequestBody Map<String, Object> map) {
        if (MapUtils.isNotEmpty(map)) {
            String appName = (String) map.get("appName");
            Query query = Query.query(Criteria.where("appName").is(appName));
            MonitoredApp existApp = mongoTemplate.findOne(query, MonitoredApp.class);
            if (Objects.isNull(existApp)) {
                MonitoredApp appToInsert = new MonitoredApp();
                appToInsert.setAppName(appName);
                String email = (String) ((LinkedHashMap) map.get("responsiblePerson")).get("email");
                ResponsiblePerson person = new ResponsiblePerson();
                person.setEmail(email);
                appToInsert.setResponsiblePerson(person);
                LinkedHashMap nodes = (LinkedHashMap) map.get("nodes");
                List<Node> nos = new ArrayList<>(nodes.size());
                for (Object k : nodes.keySet()) {
                    LinkedHashMap v = ((LinkedHashMap) nodes.get(k));
                    String serverAddress = (String) v.get("serverAddress");
                    int port = Integer.valueOf((String) v.get("port"));
                    String healthContextPath = (String) v.get("healthContextPath");
                    Node node = new Node(serverAddress, port, healthContextPath);
                    nos.add(node);
                }
                appToInsert.setNodes(nos);
                mongoTemplate.insert(appToInsert);
            }
        }
    }
}

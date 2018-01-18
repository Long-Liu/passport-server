package org.infinity.passport.repository;

import org.infinity.passport.domain.MonitoredApp;
import org.springframework.data.mongodb.repository.MongoRepository;


/**
 * Spring Data MongoDB repository for the App entity.
 */
public interface MonitoredAppRepository extends MongoRepository<MonitoredApp, String> {

}

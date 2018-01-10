package org.infinity.passport.repository;

import java.util.List;
import java.util.Optional;

import org.infinity.passport.domain.AdminMenu;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Spring Data MongoDB repository for the AdminMenu entity.
 */
public interface AdminMenuRepository extends MongoRepository<AdminMenu, String> {

    Optional<AdminMenu> findOneByAppNameAndSequence(String appName, Integer sequence);

    List<AdminMenu> findByAppNameAndIdIn(Sort sort, String appName, List<String> ids);

    List<AdminMenu> findByAppNameAndIdInAndLevelGreaterThan(String appName, List<String> ids, Integer level);

    List<AdminMenu> findByAppName(String appName);

    Page<AdminMenu> findByAppName(Pageable pageable, String appName);

    List<AdminMenu> findByAppNameAndLevel(String appName, Integer level);
}

package org.infinity.passport.service;

import java.util.List;
import java.util.Optional;

import org.infinity.passport.domain.AdminMenu;
import org.infinity.passport.dto.AdminManagedMenuDTO;
import org.infinity.passport.dto.AdminMenuDTO;

public interface AdminMenuService {

    Optional<AdminMenu> findOneByAppNameAndSequence(String appName, Integer sequence);

    AdminMenu insert(String appName, String adminMenuName, String adminMenuChineseText, String link, Integer sequence,
            String parentMenuId);

    AdminMenu update(String id, String appName, String adminMenuName, String adminMenuChineseText, Integer level,
            String link, Integer sequence, String parentMenuId);

    List<AdminManagedMenuDTO> classifyAdminMenu(List<AdminMenuDTO> data);

    List<AdminManagedMenuDTO> getAuthorityMenus(String appName, List<String> enabledAuthorities);

    List<AdminMenuDTO> getAuthorityLinks(String appName, List<String> enabledAuthorities);
}
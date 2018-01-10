//package org.infinity.passport.setup;
//
//import org.infinity.passport.domain.AdminMenu;
//import org.infinity.passport.domain.App;
//import org.infinity.passport.domain.AppAuthority;
//import org.infinity.passport.domain.Authority;
//import org.infinity.passport.domain.AuthorityAdminMenu;
//import org.infinity.passport.domain.User;
//import org.infinity.passport.domain.UserAuthority;
//import org.springframework.data.mongodb.core.MongoTemplate;
//
//import com.github.mongobee.changeset.ChangeLog;
//import com.github.mongobee.changeset.ChangeSet;
//
///**
// * Creates the initial database
// */
//@ChangeLog(order = "001")
//public class DatabaseInitialSetup {
//
//    private static final String APP_NAME = "PassportServer";
//
//    @ChangeSet(order = "01", author = "Louis", id = "addApps")
//    public void addApps(MongoTemplate mongoTemplate) {
//        App app = new App(APP_NAME, true);
//        mongoTemplate.save(app);
//    }
//
//    @ChangeSet(order = "02", author = "Louis", id = "addAuthorities")
//    public void addAuthorities(MongoTemplate mongoTemplate) {
//        mongoTemplate.save(new Authority(Authority.USER, true, true));
//        mongoTemplate.save(new Authority(Authority.ADMIN, true, true));
//        mongoTemplate.save(new Authority(Authority.DEVELOPER, true, true));
//        mongoTemplate.save(new Authority(Authority.ANONYMOUS, true, true));
//
//        mongoTemplate.save(new AppAuthority(APP_NAME, Authority.USER));
//        mongoTemplate.save(new AppAuthority(APP_NAME, Authority.ADMIN));
//        mongoTemplate.save(new AppAuthority(APP_NAME, Authority.DEVELOPER));
//        mongoTemplate.save(new AppAuthority(APP_NAME, Authority.ANONYMOUS));
//    }
//
//    @ChangeSet(order = "03", author = "Louis", id = "addUserAndAuthories")
//    public void addUserAndAuthories(MongoTemplate mongoTemplate) {
//        // Creates 'user' user and corresponding authorities
//        User userRoleUser = new User();
//        userRoleUser.setUserName("user");
//        userRoleUser.setFirstName("");
//        userRoleUser.setLastName("User");
//        userRoleUser.setEmail("user@localhost");
//        userRoleUser.setMobileNo("15000899479");
//        // Raw password: user
//        userRoleUser.setPasswordHash("$2a$10$VEjxo0jq2YG9Rbk2HmX9S.k1uZBGYUHdUcid3g/vfiEl7lwWgOH/K");
//        userRoleUser.setActivated(true);
//        userRoleUser.setActivationKey(null);
//        userRoleUser.setResetKey(null);
//        userRoleUser.setResetTime(null);
//        userRoleUser.setAvatarImageUrl(null);
//        userRoleUser.setEnabled(true);
//        mongoTemplate.save(userRoleUser);
//
//        mongoTemplate.save(new UserAuthority(userRoleUser.getId(), Authority.USER));
//
//        // Creates 'admin' user and corresponding authorities
//        User adminRoleUser = new User();
//        adminRoleUser.setUserName("admin");
//        adminRoleUser.setFirstName("");
//        adminRoleUser.setLastName("Admin");
//        adminRoleUser.setEmail("admin@localhost");
//        adminRoleUser.setMobileNo("15000899477");
//        // Raw password: admin
//        adminRoleUser.setPasswordHash("$2a$10$qlL.NVNIlHP1MgNgyKKuh.T4Am3VRTOEpw6T7siKKkx7662qxm49a");
//        adminRoleUser.setActivated(true);
//        adminRoleUser.setActivationKey(null);
//        adminRoleUser.setResetKey(null);
//        adminRoleUser.setResetTime(null);
//        adminRoleUser.setAvatarImageUrl(null);
//        adminRoleUser.setEnabled(true);
//        mongoTemplate.save(adminRoleUser);
//
//        mongoTemplate.save(new UserAuthority(adminRoleUser.getId(), Authority.USER));
//        mongoTemplate.save(new UserAuthority(adminRoleUser.getId(), Authority.ADMIN));
//
//        // Creates 'system' user and corresponding authorities
//        User adminRoleSystemUser = new User();
//        adminRoleSystemUser.setUserName("system");
//        adminRoleSystemUser.setFirstName("");
//        adminRoleSystemUser.setLastName("System");
//        adminRoleSystemUser.setEmail("system@localhost");
//        adminRoleSystemUser.setMobileNo("15000899422");
//        // Raw password: system
//        adminRoleSystemUser.setPasswordHash("$2a$10$Hm7muQK9CmOJOXLmR/hfDuss6JjYUXCe.e/Ism6hPsnIOudvuPD6u");
//        adminRoleSystemUser.setActivated(true);
//        adminRoleSystemUser.setActivationKey(null);
//        adminRoleSystemUser.setResetKey(null);
//        adminRoleSystemUser.setResetTime(null);
//        adminRoleSystemUser.setAvatarImageUrl(null);
//        adminRoleSystemUser.setEnabled(true);
//        mongoTemplate.save(adminRoleSystemUser);
//
//        mongoTemplate.save(new UserAuthority(adminRoleSystemUser.getId(), Authority.USER));
//        mongoTemplate.save(new UserAuthority(adminRoleSystemUser.getId(), Authority.ADMIN));
//
//        // Creates 'louis' user and corresponding authorities
//        User developerRoleUser = new User();
//        developerRoleUser.setUserName("louis");
//        developerRoleUser.setFirstName("");
//        developerRoleUser.setLastName("🅛🅞🅤🅘🅢");
//        developerRoleUser.setEmail("louis@localhost");
//        developerRoleUser.setMobileNo("15000899488");
//        // Raw password: louis
//        developerRoleUser.setPasswordHash("$2a$10$NTq4hNJi4HkLJqa1hJEU5enhNSG8Vhn/D5K8TkMpUOQJgWdEze7DS");
//        developerRoleUser.setActivated(true);
//        developerRoleUser.setActivationKey(null);
//        developerRoleUser.setResetKey(null);
//        developerRoleUser.setResetTime(null);
//        developerRoleUser.setAvatarImageUrl(null);
//        developerRoleUser.setEnabled(true);
//        mongoTemplate.save(developerRoleUser);
//
//        mongoTemplate.save(new UserAuthority(developerRoleUser.getId(), Authority.USER));
//        mongoTemplate.save(new UserAuthority(developerRoleUser.getId(), Authority.ADMIN));
//        mongoTemplate.save(new UserAuthority(developerRoleUser.getId(), Authority.DEVELOPER));
//    }
//
//    @ChangeSet(order = "04", author = "Louis", id = "addAuthorityAdminMenu")
//    public void addAuthorityAdminMenu(MongoTemplate mongoTemplate) {
//
//        AdminMenu adminMenu1 = new AdminMenu(APP_NAME, "user-authority", "用户权限", 1, "user-authority", 100, null);
//        mongoTemplate.save(adminMenu1);
//
//        AdminMenu adminMenu2 = new AdminMenu(APP_NAME, "authority-list", "权限", 2, "user-authority.authority-list", 101,
//                adminMenu1.getId());
//        mongoTemplate.save(adminMenu2);
//
//        AdminMenu adminMenu3 = new AdminMenu(APP_NAME, "user-list", "用户", 2, "user-authority.user-list", 102,
//                adminMenu1.getId());
//        mongoTemplate.save(adminMenu3);
//
//        AdminMenu adminMenu4 = new AdminMenu(APP_NAME, "app", "应用系统", 1, "app", 200, null);
//        mongoTemplate.save(adminMenu4);
//
//        AdminMenu adminMenu5 = new AdminMenu(APP_NAME, "app-list", "应用", 2, "app.app-list", 201, adminMenu4.getId());
//        mongoTemplate.save(adminMenu5);
//
//        AdminMenu adminMenu6 = new AdminMenu(APP_NAME, "admin-menu-authority", "管理菜单权限", 1, "admin-menu-authority", 300,
//                null);
//        mongoTemplate.save(adminMenu6);
//
//        AdminMenu adminMenu7 = new AdminMenu(APP_NAME, "admin-menu-list", "管理菜单", 2,
//                "admin-menu-authority.admin-menu-list", 301, adminMenu6.getId());
//        mongoTemplate.save(adminMenu7);
//
//        AdminMenu adminMenu8 = new AdminMenu(APP_NAME, "authority-admin-menu", "权限管理菜单", 2,
//                "admin-menu-authority.authority-admin-menu", 302, adminMenu6.getId());
//        mongoTemplate.save(adminMenu8);
//
//        AdminMenu adminMenu9 = new AdminMenu(APP_NAME, "app-monitor", "应用监控", 2, "app.app-monitor", 201, adminMenu4.getId());
//        mongoTemplate.save(adminMenu9);
//
//        AdminMenu adminMenu10 = new AdminMenu(APP_NAME, "app-config", "应用配置", 2, "app.app-config", 201, adminMenu4.getId());
//        mongoTemplate.save(adminMenu10);
//
//        AuthorityAdminMenu authorityAdminMenu1 = new AuthorityAdminMenu(Authority.ADMIN, adminMenu1.getId());
//        mongoTemplate.save(authorityAdminMenu1);
//
//        AuthorityAdminMenu authorityAdminMenu2 = new AuthorityAdminMenu(Authority.ADMIN, adminMenu2.getId());
//        mongoTemplate.save(authorityAdminMenu2);
//
//        AuthorityAdminMenu authorityAdminMenu3 = new AuthorityAdminMenu(Authority.ADMIN, adminMenu3.getId());
//        mongoTemplate.save(authorityAdminMenu3);
//
//        AuthorityAdminMenu authorityAdminMenu4 = new AuthorityAdminMenu(Authority.ADMIN, adminMenu4.getId());
//        mongoTemplate.save(authorityAdminMenu4);
//
//        AuthorityAdminMenu authorityAdminMenu5 = new AuthorityAdminMenu(Authority.ADMIN, adminMenu5.getId());
//        mongoTemplate.save(authorityAdminMenu5);
//
//        AuthorityAdminMenu authorityAdminMenu6 = new AuthorityAdminMenu(Authority.ADMIN, adminMenu6.getId());
//        mongoTemplate.save(authorityAdminMenu6);
//
//        AuthorityAdminMenu authorityAdminMenu7 = new AuthorityAdminMenu(Authority.ADMIN, adminMenu7.getId());
//        mongoTemplate.save(authorityAdminMenu7);
//
//        AuthorityAdminMenu authorityAdminMenu8 = new AuthorityAdminMenu(Authority.ADMIN, adminMenu8.getId());
//        mongoTemplate.save(authorityAdminMenu8);
//
//        AuthorityAdminMenu authorityAdminMenu9 = new AuthorityAdminMenu(Authority.ADMIN, adminMenu9.getId());
//        mongoTemplate.save(authorityAdminMenu9);
//
//        AuthorityAdminMenu authorityAdminMenu10 = new AuthorityAdminMenu(Authority.ADMIN, adminMenu10.getId());
//        mongoTemplate.save(authorityAdminMenu10);
//    }
//}

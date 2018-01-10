package org.infinity.passport.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.infinity.passport.config.ApplicationConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.boot.SpringApplication;
import org.springframework.core.io.ClassPathResource;

/**
 * Utility class to load a Spring profile to be used as default when there is no
 * <code>spring.profiles.active</code> set in the environment or as command line
 * argument. If the value is not available in <code>application.yml</code> then
 * <code>dev</code> profile will be used as default.
 */
public final class ApplicationPropertiesUtils {

    private static final Logger    LOGGER                 = LoggerFactory.getLogger(ApplicationPropertiesUtils.class);

    public static final Properties APPLICATION_PROPERTIES = readApplicationProperties();

    /**
     * Set a default to use when no profile is configured.
     */
    public static void addDefaultProfile(SpringApplication app) {
        Map<String, Object> defProperties = new HashMap<String, Object>();
        /*
         * The default profile to use when no other profiles are defined This
         * cannot be set in the <code>application.yml</code> file. See
         * https://github.com/spring-projects/spring-boot/issues/1219
         */
        defProperties.put(ApplicationConstants.SPRING_PROFILES_ACTIVE, getDefaultActiveProfiles());
        app.setDefaultProperties(defProperties);
    }

    /**
     * Set some environment variables
     */
    public static void addEnvVariables() {
        if (StringUtils.isBlank(System.getProperty("logging.path"))
                && StringUtils.isBlank(ApplicationPropertiesUtils.APPLICATION_PROPERTIES.getProperty("logging.path"))) {
            System.setProperty("logging.path", FileUtils.getUserDirectoryPath() + IOUtils.DIR_SEPARATOR + "Log");
        }
        if (StringUtils.isBlank(System.getProperty("logback.loglevel"))) {
            System.setProperty("logback.loglevel", "DEBUG");
        }
        if (StringUtils.isBlank(System.getProperty("dubbo.registry.file"))) {
            System.setProperty("dubbo.registry.file",
                    FileUtils.getUserDirectoryPath() + IOUtils.DIR_SEPARATOR + "Dubbo" + IOUtils.DIR_SEPARATOR
                            + ApplicationPropertiesUtils.APPLICATION_PROPERTIES.getProperty("info.artifact.name")
                            + "-dubbo-registry.properties");
        }
        System.setProperty("app.package", ApplicationConstants.BASE_PACKAGE);

        if (StringUtils.isNotBlank(
                ApplicationPropertiesUtils.APPLICATION_PROPERTIES.getProperty("spring.dubbo.application.logger"))) {
            // Specify slf4j, Plz refer com.alibaba.dubbo.common.logger.LoggerFactory
            System.setProperty("dubbo.application.logger",
                    ApplicationPropertiesUtils.APPLICATION_PROPERTIES.getProperty("spring.dubbo.application.logger"));
        }
    }

    /**
     * Get a default profile from <code>application.yml</code>.
     */
    public static String getDefaultActiveProfiles() {
        if (APPLICATION_PROPERTIES != null) {
            String activeProfile = APPLICATION_PROPERTIES.getProperty(ApplicationConstants.SPRING_PROFILES_ACTIVE);
            if (activeProfile != null && !activeProfile.isEmpty()) {
                return activeProfile;
            }
        }
        LOGGER.warn("No Spring profile configured, running with default profile: {}",
                ApplicationConstants.SPRING_PROFILE_DEVELOPMENT);
        return ApplicationConstants.SPRING_PROFILE_DEVELOPMENT;
    }

    /**
     * Load application.yml from classpath.
     */
    private static Properties readApplicationProperties() {
        try {
            YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();
            factory.setResources(new ClassPathResource("config/application.yml"));
            return factory.getObject();
        } catch (Exception e) {
            LOGGER.error("Failed to read application.yml");
        }
        return null;
    }
}

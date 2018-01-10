package org.infinity.passport.config;

import java.util.Locale;

/**
 * Application constants.
 */
public interface ApplicationConstants {

    String BASE_PACKAGE = "org.infinity.passport";

    // Spring profile
    String SPRING_PROFILES_ACTIVE = "spring.profiles.active";

    String SPRING_PROFILE_DEVELOPMENT = "dev";

    String SPRING_PROFILE_TEST = "test";

    String SPRING_PROFILE_DEMO = "demo";

    String SPRING_PROFILE_PRODUCTION = "prod";

    // Spring profile used to disable swagger
    String SPRING_PROFILE_NO_SWAGGER = "no-swagger";

    // Spring profile used to disable AOP logging
    String SPRING_PROFILE_NO_AOP_LOGGING = "no-aop-logging";

    // Spring profile used to enable service metrics
    String SPRING_PROFILE_SERVICE_METRICS = "service-metrics";

    // Spring profile used to tracing request with Spring Cloud Zipkin
    String SPRING_PROFILE_ZIPKIN = "zipkin";

    String[] AVAILABLE_PROFILES = new String[]{SPRING_PROFILE_DEVELOPMENT,
            SPRING_PROFILE_TEST, SPRING_PROFILE_DEMO, SPRING_PROFILE_PRODUCTION, SPRING_PROFILE_NO_SWAGGER,
            SPRING_PROFILE_NO_AOP_LOGGING, SPRING_PROFILE_ZIPKIN, SPRING_PROFILE_SERVICE_METRICS};

    String SYSTEM_ACCOUNT = "system";

    String SCHEDULE_LOG_PATTERN = "########################Schedule executed: {}########################";

    Locale SYSTEM_LOCALE = Locale.SIMPLIFIED_CHINESE;

}

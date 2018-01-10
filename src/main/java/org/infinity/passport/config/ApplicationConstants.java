package org.infinity.passport.config;

import java.util.Locale;

/**
 * Application constants.
 */
public final class ApplicationConstants {

    public static final String   BASE_PACKAGE                   = "org.infinity.passport";

    // Spring profile
    public static final String   SPRING_PROFILES_ACTIVE         = "spring.profiles.active";

    public static final String   SPRING_PROFILE_DEVELOPMENT     = "dev";

    public static final String   SPRING_PROFILE_TEST            = "test";

    public static final String   SPRING_PROFILE_DEMO            = "demo";

    public static final String   SPRING_PROFILE_PRODUCTION      = "prod";

    // Spring profile used to disable swagger
    public static final String   SPRING_PROFILE_NO_SWAGGER      = "no-swagger";

    // Spring profile used to disable AOP logging
    public static final String   SPRING_PROFILE_NO_AOP_LOGGING  = "no-aop-logging";

    // Spring profile used to enable service metrics
    public static final String   SPRING_PROFILE_SERVICE_METRICS = "service-metrics";

    // Spring profile used to tracing request with Spring Cloud Zipkin
    public static final String   SPRING_PROFILE_ZIPKIN          = "zipkin";

    public static final String[] AVAILABLE_PROFILES             = new String[] { SPRING_PROFILE_DEVELOPMENT,
            SPRING_PROFILE_TEST, SPRING_PROFILE_DEMO, SPRING_PROFILE_PRODUCTION, SPRING_PROFILE_NO_SWAGGER,
            SPRING_PROFILE_NO_AOP_LOGGING, SPRING_PROFILE_ZIPKIN, SPRING_PROFILE_SERVICE_METRICS };

    public static final String   SYSTEM_ACCOUNT                 = "system";

    public static final String   SCHEDULE_LOG_PATTERN           = "########################Schedule executed: {}########################";

    public static final Locale   SYSTEM_LOCALE                  = Locale.SIMPLIFIED_CHINESE;

    private ApplicationConstants() {
    }
}

package org.infinity.passport.config;

import java.util.Arrays;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Aspect for logging execution of service and controller Spring components.
 */
@Aspect
@Profile("!" + ApplicationConstants.SPRING_PROFILE_NO_AOP_LOGGING)
@Configuration
public class LoggingAspectConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingAspectConfiguration.class);

    // @Autowired
    // private Environment env;

    @Pointcut("within(" + ApplicationConstants.BASE_PACKAGE + ".service..*) || within(" + ApplicationConstants.BASE_PACKAGE
            + ".controller..*)")
    public void loggingPointcut() {
    }

    // @AfterThrowing(pointcut = "loggingPointcut()", throwing = "e")
    // public void logAfterThrowing(JoinPoint joinPoint, Throwable e) {
    // if (env.acceptsProfiles(Constants.SPRING_PROFILE_DEVELOPMENT)) {
    // log.error("Exception in {}.{}() with cause = {} and exception {}",
    // joinPoint.getSignature().getDeclaringTypeName(),
    // joinPoint.getSignature().getName(), e.getCause(),
    // e);
    // } else {
    // log.error("Exception in {}.{}() with cause = {}",
    // joinPoint.getSignature().getDeclaringTypeName(),
    // joinPoint.getSignature().getName(), e.getCause());
    // }
    // }

    @Around("loggingPointcut()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Enter: {}.{}() with argument[s] = {}", joinPoint.getSignature().getDeclaringTypeName(),
                    joinPoint.getSignature().getName(), Arrays.toString(joinPoint.getArgs()));
        }
        try {
            Object result = joinPoint.proceed();
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Exit: {}.{}() with result = {}", joinPoint.getSignature().getDeclaringTypeName(),
                        joinPoint.getSignature().getName(), result);
            }
            return result;
        }
        catch (IllegalArgumentException e) {
            LOGGER.error("Illegal argument: {} in {}.{}()", Arrays.toString(joinPoint.getArgs()),
                    joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName());

            throw e;
        }
    }
}

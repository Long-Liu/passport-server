package org.infinity.passport.config;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.infinity.passport.config.lock.RedisDistributedLock;
import org.infinity.passport.config.lock.annotation.RedisLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.Arrays;

@Aspect
@Configuration
@ConditionalOnClass(RedisDistributedLock.class)
@AutoConfigureAfter(DistributedLockAutoConfiguration.class)
public class DistributedLockAspectConfiguration {

    private final Logger logger = LoggerFactory.getLogger(DistributedLockAspectConfiguration.class);

    private final RedisDistributedLock redisDistributedLock;

    @Autowired
    public DistributedLockAspectConfiguration(RedisDistributedLock redisDistributedLock) {
        this.redisDistributedLock = redisDistributedLock;
    }

    @Pointcut("@annotation(org.infinity.passport.config.lock.annotation.RedisLock)")
    private void lockPoint(){
    }

    @Around("lockPoint()")
    public Object around(ProceedingJoinPoint point) throws Throwable{
        Method method = ((MethodSignature) point.getSignature()).getMethod();
        RedisLock redisLock = method.getAnnotation(RedisLock.class);
        String key = redisLock.value();
        if(StringUtils.isEmpty(key)){
            Object[] args = point.getArgs();
            key = Arrays.toString(args);
        }
        int retryTimes = redisLock.action().equals(RedisLock.LockFailAction.CONTINUE) ? redisLock.retryTimes() : 0;
        boolean lock = redisDistributedLock.lock(key, redisLock.keepMills(), retryTimes, redisLock.sleepMills());
        if(!lock) {
            logger.debug("get lock failed : " + key);
            return null;
        }
        logger.debug("get lock success : " + key);
        try {
            return point.proceed();
        } catch (Exception e) {
            logger.error("execute locked method occured an exception", e);
        } finally {
            boolean releaseResult = redisDistributedLock.releaseLock(key);
            logger.debug("release lock : " + key + (releaseResult ? " success" : " failed"));
        }
        return null;
    }
}

package org.infinity.passport.config.lock;

public interface DistributedLock {
    long TIMEOUT_MILLIS = 300;

    int RETRY_TIMES = Integer.MAX_VALUE;

    long SLEEP_MILLIS = 500;

    boolean lock(String key);

    boolean lock(String key, int retryTimes);

    boolean lock(String key, int retryTimes, long sleepMillis);

    boolean lock(String key, long expire);

    boolean lock(String key, long expire, int retryTimes);

    boolean lock(String key, long expire, int retryTime, long sleepMillis);

    boolean releaseLock(String key);
}

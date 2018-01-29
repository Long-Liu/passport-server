package org.infinity.passport.config.lock;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisCommands;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RedisDistributedLock extends AbstractDistributedLock {

    private static final Logger log = LoggerFactory.getLogger(RedisDistributedLock.class);

    private final RedisTemplate<String, Object> redisTemplate;

    private final ThreadLocal<String> lockFlag = new ThreadLocal<>();

    private static final String UNLOCK_LUA;

    static {
        UNLOCK_LUA = "if redis.call(\"get\",KEYS[1]) == ARGV[1] " +
                "then " +
                "    return redis.call(\"del\",KEYS[1]) " +
                "else " +
                "    return 0 " +
                "end ";
    }

    public RedisDistributedLock(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public boolean lock(String key, long expire, int retryTimes, long sleepMillis) {
        boolean result = setRedis(key, expire);
        while ((!result) && retryTimes-- > 0) {
            try {
                log.debug("lock failed, retrying..." + retryTimes);
                Thread.sleep(sleepMillis);
            } catch (InterruptedException e) {
                return false;
            }
            result = setRedis(key, expire);
        }
        return result;
    }

    @Override
    public boolean releaseLock(String key) {
        try {
            List<String> keys = new ArrayList<>();
            keys.add(key);
            List<String> args = new ArrayList<>();
            args.add(lockFlag.get());
            Long result = redisTemplate.execute((RedisCallback<Long>) connection -> {
                Object nativeConnection = connection.getNativeConnection();
                if (nativeConnection instanceof JedisCluster) {
                    return (Long) ((JedisCluster) nativeConnection).eval(UNLOCK_LUA, keys, args);
                } else if (nativeConnection instanceof Jedis) {
                    return (Long) ((Jedis) nativeConnection).eval(UNLOCK_LUA, keys, args);
                }
                return 0L;
            });
            return result != null && result > 0;
        } catch (Exception e) {
            log.error("release lock occured an exception", e);
        }
        return false;
    }

    private boolean setRedis(String key, long expire) {
        try {
            String result = redisTemplate.execute((RedisCallback<String>) connection -> {
                JedisCommands commands = (JedisCommands) connection.getNativeConnection();
                String uuid = UUID.randomUUID().toString();
                lockFlag.set(uuid);
                return commands.set(key, uuid, "NX", "PX", expire);
            });
            return !StringUtils.isEmpty(result);
        } catch (Exception e) {
            log.error("set redis occured an exception", e);
        }
        return false;
    }
}

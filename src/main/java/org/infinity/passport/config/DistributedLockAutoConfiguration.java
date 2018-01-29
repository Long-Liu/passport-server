package org.infinity.passport.config;

import org.infinity.passport.config.lock.RedisDistributedLock;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
@AutoConfigureAfter(RedisAutoConfiguration.class)
public class DistributedLockAutoConfiguration {

    @Bean
    public RedisDistributedLock redisDistributedLock(RedisTemplate<String, Object> redisTemplate){
        return new RedisDistributedLock(redisTemplate);
    }

}

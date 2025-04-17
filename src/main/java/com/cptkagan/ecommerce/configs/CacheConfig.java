package com.cptkagan.ecommerce.configs;

import java.time.Duration;

import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

@Configuration
public class CacheConfig {
    
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory){
        // Default cache configuration with a time-to-live of 10 mins
        RedisCacheConfiguration config= RedisCacheConfiguration.defaultCacheConfig()
                            .entryTtl(Duration.ofMinutes(10));

        // Create and return a RedisCacheManager with the specified configuration
        return RedisCacheManager.builder(redisConnectionFactory)
                                .cacheDefaults(config)
                                .build();
    }
}

package org.poem.config;

import org.poem.utils.RedisUtils;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.cache.CacheManager;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import redis.clients.jedis.JedisPoolConfig;

/**
 * spring data redis config
 */
@Configuration
@EnableTransactionManagement
public class RedisConfig implements EnvironmentAware {
    /**
     * 环境变量
     */
    private Environment env;
    /**
     * 属性设置
     */
    private RelaxedPropertyResolver redisPropertyResolver;

    @Override
    public void setEnvironment(Environment environment) {
        this.env = environment;
        this.redisPropertyResolver = new RelaxedPropertyResolver(env, "spring.redis.");
    }

    @Bean
    public JedisPoolConfig getJedisPoolConfig() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        /*控制一个pool最多有多少个状态为idle(空闲)的jedis实例*/
        jedisPoolConfig.setMinIdle(20);
        jedisPoolConfig.setMaxIdle(50);
        jedisPoolConfig.setMaxTotal(100);
        jedisPoolConfig.setMaxWaitMillis(10000);
        /*在return给pool时，是否提前进行validate操作*/
        jedisPoolConfig.setTestOnBorrow(false);
        return jedisPoolConfig;
    }

    @Bean
    public JedisConnectionFactory getJedisConnectionFactory() {
        JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory();

        jedisConnectionFactory.setUsePool(true);
        jedisConnectionFactory.setHostName(redisPropertyResolver.getProperty("host"));
        jedisConnectionFactory.setPort(Integer.valueOf(redisPropertyResolver.getProperty("port")));
        jedisConnectionFactory.setTimeout(30000);
        jedisConnectionFactory.setDatabase(1);
        jedisConnectionFactory.setPoolConfig(getJedisPoolConfig());
        return jedisConnectionFactory;
    }

    @Bean
    public RedisTemplate getRedisTemplate() {
        RedisTemplate redisTemplate = new RedisTemplate();
        redisTemplate.setConnectionFactory(getJedisConnectionFactory());
        return redisTemplate;
    }

    /**
     * 获取缓存设置
     * @return
     */
    @Bean
    public CacheManager cacheManager() {
        new RedisUtils(getRedisTemplate());
        RedisCacheManager cacheManager = new RedisCacheManager(getRedisTemplate());
        cacheManager.setDefaultExpiration(300);
        return cacheManager;
    }
}

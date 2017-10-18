package com.doopp.gauss.server.configuration;

import com.doopp.gauss.server.redis.CustomShadedJedis;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedisPool;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class RedisConfiguration {

    @Bean
    public CustomShadedJedis roomRedis(@Qualifier("jedisPoolConfig") JedisPoolConfig jedisPoolConfig)
    {
        // shard redis
        ShardedJedisPool shardedJedisPool = this.shardedJedisPool(jedisPoolConfig, "redis://127.0.0.1:6379/1", "redis://127.0.0.1:6379/2");
        CustomShadedJedis customShadedJedis = new CustomShadedJedis();
        customShadedJedis.setShardedJedisPool(shardedJedisPool);
        return customShadedJedis;
    }

    @Bean
    public CustomShadedJedis roomIndexRedis(@Qualifier("jedisPoolConfig") JedisPoolConfig jedisPoolConfig)
    {
        // shard redis
        ShardedJedisPool shardedJedisPool = this.shardedJedisPool(jedisPoolConfig, "redis://127.0.0.1:6379/3", "redis://127.0.0.1:6379/4");
        CustomShadedJedis customShadedJedis = new CustomShadedJedis();
        customShadedJedis.setShardedJedisPool(shardedJedisPool);
        return customShadedJedis;
    }

    @Bean
    public CustomShadedJedis sessionRedis(@Qualifier("jedisPoolConfig") JedisPoolConfig jedisPoolConfig)
    {
        ShardedJedisPool shardedJedisPool = this.shardedJedisPool(jedisPoolConfig, "redis://127.0.0.1:6379/5", "redis://127.0.0.1:6379/6");
        CustomShadedJedis customShadedJedis = new CustomShadedJedis();
        customShadedJedis.setShardedJedisPool(shardedJedisPool);
        return customShadedJedis;
    }

    @Bean
    public JedisPoolConfig jedisPoolConfig ()
    {
        // Jedis池配置
        JedisPoolConfig config = new JedisPoolConfig();
        // 最大分配的对象数
        config.setMaxTotal(10);
        // 最大能够保持idel状态的对象数
        config.setMaxIdle(10);
        // 最小空闲的对象数。2.5.1以上版本有效
        config.setMinIdle(8);
        // 当池内没有返回对象时，最大等待时间
        config.setMaxWaitMillis(1000);
        // 是否启用Lifo。如果不设置，默认为true。2.5.1以上版本有效
        config.setLifo(false);
        // 当调用borrow Object方法时，是否进行有效性检查
        config.setTestOnBorrow(true);
        // return
        return config;
    }

    //    @Bean
    //    public JedisConnectionFactory jedisConnectionFactory(@Qualifier("jedisPoolConfig") JedisPoolConfig jedisPoolConfig)
    //    {
    //        // JedisConnectionFactory setting
    //        JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory();
    //        jedisConnectionFactory.setPoolConfig(jedisPoolConfig);
    //        jedisConnectionFactory.setHostName("127.0.0.1");
    //        jedisConnectionFactory.setPort(6379);
    //        jedisConnectionFactory.setPassword("");
    //        jedisConnectionFactory.setDatabase(7);
    //        jedisConnectionFactory.setTimeout(2000);
    //        return jedisConnectionFactory;
    //    }
    //
    //    @Bean
    //    public RedisTemplate templateRedis(@Qualifier("jedisConnectionFactory") JedisConnectionFactory connectionFactory)
    //    {
    //        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
    //        redisTemplate.setConnectionFactory(connectionFactory);
    //        redisTemplate.setKeySerializer(new StringRedisSerializer());
    //        redisTemplate.setValueSerializer(new JdkSerializationRedisSerializer());
    //        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
    //        return redisTemplate;
    //    }

    private ShardedJedisPool shardedJedisPool(JedisPoolConfig jedisPoolConfig, String... hosts)
    {
        // map host
        List<JedisShardInfo> jedisInfoList =new ArrayList<>(hosts.length);
        // loop
        for (String host : hosts) {
            JedisShardInfo jedisShardInfo = new JedisShardInfo(host);
            jedisShardInfo.setConnectionTimeout(2000);
            jedisShardInfo.setSoTimeout(2000);
            jedisInfoList.add(jedisShardInfo);
        }
        // return ShardedJedisPool
        return new ShardedJedisPool(jedisPoolConfig, jedisInfoList);
    }
}

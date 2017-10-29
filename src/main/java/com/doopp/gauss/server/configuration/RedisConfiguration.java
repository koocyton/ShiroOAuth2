package com.doopp.gauss.server.configuration;

import com.doopp.gauss.server.redis.CustomShadedJedis;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedisPool;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class RedisConfiguration {

    //    @Bean
    //    public CustomShadedJedis roomRedis(JedisPoolConfig jedisPoolConfig, Properties applicationProperties)
    //    {
    //        String s1 = applicationProperties.getProperty("redis.server.room.1");
    //        String s2 = applicationProperties.getProperty("redis.server.room.2");
    //        // shard redis
    //        ShardedJedisPool shardedJedisPool = this.shardedJedisPool(jedisPoolConfig, s1, s2);
    //        CustomShadedJedis customShadedJedis = new CustomShadedJedis();
    //        customShadedJedis.setShardedJedisPool(shardedJedisPool);
    //        return customShadedJedis;
    //    }
    //
    //    @Bean
    //    public CustomShadedJedis roomIndexRedis(JedisPoolConfig jedisPoolConfig, Properties applicationProperties)
    //    {
    //        String s1 = applicationProperties.getProperty("redis.server.roomIndex.1");
    //        String s2 = applicationProperties.getProperty("redis.server.roomIndex.2");
    //        // shard redis
    //        ShardedJedisPool shardedJedisPool = this.shardedJedisPool(jedisPoolConfig, s1, s2);
    //        CustomShadedJedis customShadedJedis = new CustomShadedJedis();
    //        customShadedJedis.setShardedJedisPool(shardedJedisPool);
    //        return customShadedJedis;
    //    }

    @Bean
    public CustomShadedJedis sessionRedis(@Qualifier("jedisPoolConfig") JedisPoolConfig jedisPoolConfig, @Qualifier("applicationProperties") Properties applicationProperties)
    {
        String s1 = applicationProperties.getProperty("redis.server.session.1");
        String s2 = applicationProperties.getProperty("redis.server.session.2");
        //System.out.print(" >>>>>>>>>>>>>>> " + s1 + " \n >>>>>>>>>>>>>>> " + s2 + "\n");
        // shard redis
        ShardedJedisPool shardedJedisPool = this.shardedJedisPool(jedisPoolConfig, s1, s2);
        CustomShadedJedis customShadedJedis = new CustomShadedJedis();
        customShadedJedis.setShardedJedisPool(shardedJedisPool);
        return customShadedJedis;
    }

    @Bean
    public CustomShadedJedis userRedis(@Qualifier("jedisPoolConfig") JedisPoolConfig jedisPoolConfig, @Qualifier("applicationProperties") Properties applicationProperties)
    {
        String s1 = applicationProperties.getProperty("redis.server.user.1");
        String s2 = applicationProperties.getProperty("redis.server.user.2");
        //System.out.print(" >>>>>>>>>>>>>>> " + s1 + " \n >>>>>>>>>>>>>>> " + s2 + "\n");
        // shard redis
        ShardedJedisPool shardedJedisPool = this.shardedJedisPool(jedisPoolConfig, s1, s2);
        CustomShadedJedis customShadedJedis = new CustomShadedJedis();
        customShadedJedis.setShardedJedisPool(shardedJedisPool);
        return customShadedJedis;
    }

    @Bean
    public JedisPoolConfig jedisPoolConfig (Properties applicationProperties)
    {
        int maxTotal = Integer.parseInt(applicationProperties.getProperty("redis.pool.maxTotal"));
        int maxIdle = Integer.parseInt(applicationProperties.getProperty("redis.pool.maxIdle"));
        int minIdle = Integer.parseInt(applicationProperties.getProperty("redis.pool.minIdle"));
        boolean lifo = Boolean.getBoolean(applicationProperties.getProperty("redis.pool.lifo"));
        int maxWaitMillis = Integer.parseInt(applicationProperties.getProperty("redis.pool.maxWaitMillis"));
        boolean testOnBorrow = Boolean.getBoolean(applicationProperties.getProperty("redis.pool.testOnBorrow"));

        // Jedis池配置
        JedisPoolConfig config = new JedisPoolConfig();
        // 最大分配的对象数
        config.setMaxTotal(maxTotal);
        // 最大能够保持idel状态的对象数
        config.setMaxIdle(maxIdle);
        // 最小空闲的对象数。2.5.1以上版本有效
        config.setMinIdle(minIdle);
        // 当池内没有返回对象时，最大等待时间
        config.setMaxWaitMillis(maxWaitMillis);
        // 是否启用Lifo。如果不设置，默认为true。2.5.1以上版本有效
        config.setLifo(lifo);
        // 当调用borrow Object方法时，是否进行有效性检查
        config.setTestOnBorrow(testOnBorrow);
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
            // System.out.print(" >>>>>>>>>>>>>>> " + host + "\n");
            JedisShardInfo jedisShardInfo = new JedisShardInfo(host);
            jedisShardInfo.setConnectionTimeout(2000);
            jedisShardInfo.setSoTimeout(2000);
            jedisInfoList.add(jedisShardInfo);
        }
        // return ShardedJedisPool
        return new ShardedJedisPool(jedisPoolConfig, jedisInfoList);
    }
}

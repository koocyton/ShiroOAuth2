package com.doopp.gauss.server.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

import java.util.Set;

public class CustomShadedJedis {

    private final Logger logger = LoggerFactory.getLogger(CustomShadedJedis.class);

    private ShardedJedisPool shardedJedisPool;

    private final JdkSerializationRedisSerializer redisSerializer = new JdkSerializationRedisSerializer();

    public void set(String key, String value) {
        ShardedJedis shardedJedis = shardedJedisPool.getResource();
        shardedJedis.set(key, value);
        shardedJedis.close();
    }

    public String get(String key) {
        ShardedJedis shardedJedis = shardedJedisPool.getResource();
        String value = shardedJedis.get(key);
        shardedJedis.close();
        return value;
    }

    public void set(byte[] key, Object object) {
        ShardedJedis shardedJedis = shardedJedisPool.getResource();
        byte[] _byte = redisSerializer.serialize(object);
        shardedJedis.set(key, _byte);
        shardedJedis.close();
    }

    public Object get(byte[] key) {
        ShardedJedis shardedJedis = shardedJedisPool.getResource();
        byte[] _byte = shardedJedis.get(key);
        Object _object = redisSerializer.deserialize(_byte);
        logger.info(" >>> redisSerializer.deserialize(_byte) " + _object);
        shardedJedis.close();
        return _object;
    }

    public void del(String... keys) {
        ShardedJedis shardedJedis = shardedJedisPool.getResource();
        for (String key : keys) {
            byte[] _key = key.getBytes();
            shardedJedis.del(_key);
        }
        shardedJedis.close();
    }

    public void del(int... keys) {
        ShardedJedis shardedJedis = shardedJedisPool.getResource();
        for (int key : keys) {
            byte[] _key = String.valueOf(key).getBytes();
            shardedJedis.del(_key);
        }
        shardedJedis.close();
    }

    // 设置空闲房间
    public Long hset(String key, String field, String value) {
        ShardedJedis shardedJedis = shardedJedisPool.getResource();
        Long result = shardedJedis.hset(key, field, value);
        shardedJedis.close();
        return result;
    }

    public String hget(String key, String field) {
        ShardedJedis shardedJedis = shardedJedisPool.getResource();
        String result = shardedJedis.hget(key, field);
        shardedJedis.close();
        return result;
    }

    public Long hdel(String key, String... fields) {
        ShardedJedis shardedJedis = shardedJedisPool.getResource();
        Long result = shardedJedis.hdel(key, fields);
        shardedJedis.close();
        return result;
    }

    public Set<String> hkeys(String key) {
        ShardedJedis shardedJedis = shardedJedisPool.getResource();
        Set<String> keys = shardedJedis.hkeys(key);
        shardedJedis.close();
        return keys;
    }

    public void setShardedJedisPool(ShardedJedisPool shardedJedisPool) {
        this.shardedJedisPool = shardedJedisPool;
    }
}

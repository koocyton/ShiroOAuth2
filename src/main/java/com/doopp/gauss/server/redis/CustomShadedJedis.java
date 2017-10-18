package com.doopp.gauss.server.redis;

import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import redis.clients.jedis.ShardedJedis;

public class CustomShadedJedis {

    private ShardedJedis shardedJedis;

    private final JdkSerializationRedisSerializer redisSerializer = new JdkSerializationRedisSerializer();

    public void set(String key, String value) {
        shardedJedis.set(key, value);
    }

    public String get(String key) {
        return shardedJedis.get(key);
    }

    public void set(byte[] key, Object object) {
        byte[] byteRoom = redisSerializer.serialize(object);
        shardedJedis.set(key, byteRoom);
    }

    public Object get(byte[] key) {
        byte[] byteRoom = shardedJedis.get(key);
        return redisSerializer.deserialize(byteRoom);
    }

    public void del(String... keys) {
        for (String key : keys) {
            byte[] byteRoomKey = key.getBytes();
            shardedJedis.del(byteRoomKey);
        }
    }

    public void del(int... keys) {
        for (int key : keys) {
            byte[] byteRoomKey = String.valueOf(key).getBytes();
            shardedJedis.del(byteRoomKey);
        }
    }

    public void setShardedJedis(ShardedJedis shardedJedis) {
        this.shardedJedis = shardedJedis;
    }
}

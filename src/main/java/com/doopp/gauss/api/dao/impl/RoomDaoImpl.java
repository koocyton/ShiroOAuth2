package com.doopp.gauss.api.dao.impl;

import com.doopp.gauss.api.dao.RoomDao;
import com.doopp.gauss.api.entity.RoomEntity;
import com.doopp.gauss.server.redis.CustomShadedJedis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("roomDao")
public class RoomDaoImpl implements RoomDao {

    @Autowired
    CustomShadedJedis roomRedis;

    @Autowired
    CustomShadedJedis roomIndexRedis;

    @Override
    public RoomEntity fetchById(int id) {
        byte[] roomKey = String.valueOf(id).getBytes();
        Object roomObject = roomRedis.get(roomKey);
        if (roomObject!=null) {
            return (RoomEntity) roomObject;
        }
        return null;
    }

    @Override
    public void create(RoomEntity room) {
        byte[] roomKey = String.valueOf(room.getId()).getBytes();
        roomRedis.set(roomKey, room);
    }

    @Override
    public void update(RoomEntity room) {
        byte[] roomKey = String.valueOf(room.getId()).getBytes();
        roomRedis.set(roomKey, room);
    }

    @Override
    public void delete(int id) {
        String roomKey = String.valueOf(id);
        roomRedis.del(roomKey);
    }
}

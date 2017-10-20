package com.doopp.gauss.api.dao.impl;

import com.doopp.gauss.api.dao.RoomDao;
import com.doopp.gauss.api.entity.RoomEntity;
import com.doopp.gauss.api.service.impl.RoomServiceImpl;
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
        // 自增长的 id
        synchronized (RoomDaoImpl.class) {
            String lastRoomId = roomRedis.get("newRoomId");
            if (lastRoomId==null) {
                lastRoomId = "10086";
            }
            int newRoomId = 1 + Integer.valueOf(lastRoomId);
            roomRedis.set("newRoomId", String.valueOf(newRoomId));
            // 设定房间编号
            room.setId(newRoomId);
        }
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

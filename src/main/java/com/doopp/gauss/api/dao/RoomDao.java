package com.doopp.gauss.api.dao;

import com.doopp.gauss.api.entity.RoomAbstractEntity;
import com.doopp.gauss.api.entity.RoomEntity;
import com.doopp.gauss.server.redis.CustomShadedJedis;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class RoomDao {

    private final Logger logger = LoggerFactory.getLogger(RoomDao.class);

    @Autowired
    CustomShadedJedis roomRedis;

    @Autowired
    CustomShadedJedis roomIndexRedis;

    public RoomEntity fetchById(int id) {
        byte[] roomKey = String.valueOf(id).getBytes();
        Object roomObject = roomRedis.get(roomKey);
        if (roomObject!=null) {
            return (RoomEntity) roomObject;
        }
        return null;
    }

    public RoomEntity constructOne() {
        RoomEntity room = new RoomEntity();
        // 自增长的 id
        synchronized ("constructOneRoom") {
            String lastRoomId = roomIndexRedis.get("lastRoomId");
            if (lastRoomId==null) {
                lastRoomId = "10086";
            }
            int newRoomId = 1 + Integer.valueOf(lastRoomId);
            roomIndexRedis.set("lastRoomId", String.valueOf(newRoomId));
            // 设定房间编号
            room.setId(newRoomId);
        }
        return room;
    }

    public List<RoomEntity> fetchList(int offset, int limit) {
        return null;
    }

    public RoomEntity create(RoomEntity room) throws Exception {
        byte[] roomKey = String.valueOf(room.getId()).getBytes();
        roomRedis.set(roomKey, room);
        Object roomObject = roomRedis.get(roomKey);
        if (roomObject==null) {
            throw new Exception("can not save room info");
        }
        return (RoomEntity) roomObject;
    }

    public void update(RoomEntity room) {
        byte[] roomKey = String.valueOf(room.getId()).getBytes();
        roomRedis.set(roomKey, room);
    }

    public int getUserIndex(Long userId) {
        String roomId = roomIndexRedis.get(userId.toString());
        if (roomId==null) {
            return 0;
        }
        return Integer.valueOf(roomId);
    }

    public void setUserIndex(Long userId, int roomId) {
        roomIndexRedis.set(String.valueOf(userId), String.valueOf(roomId));
    }

    public void delUserIndex(Long userId) {
        roomIndexRedis.del(String.valueOf(userId));
    }

    public void delete(int id) {
        byte[] roomKey = String.valueOf(id).getBytes();
        // String roomKey = String.valueOf(id);
        roomRedis.del(roomKey);
    }

    public RoomAbstractEntity getRoomAbstract(RoomEntity room) {
        RoomAbstractEntity roomAbstract = new RoomAbstractEntity();
        roomAbstract.setId(room.getId());
        roomAbstract.setName(room.getName());
        roomAbstract.setOwnerName(room.getOwner().getNickname());
        roomAbstract.setUserNumber(1);
        roomAbstract.setHot(0);
        return roomAbstract;
    }
}

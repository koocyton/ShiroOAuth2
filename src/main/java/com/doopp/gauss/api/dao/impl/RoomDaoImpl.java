//package com.doopp.gauss.api.dao.impl;
//
//import com.doopp.gauss.api.dao.RoomDao;
//import com.doopp.gauss.api.entity.RoomAbstractEntity;
//import com.doopp.gauss.api.entity.RoomEntity;
//import com.doopp.gauss.api.entity.UserEntity;
//import com.doopp.gauss.server.redis.CustomShadedJedis;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//import java.util.Map;
//
//@Repository
//public class RoomDaoImpl implements RoomDao {
//
//    private final Logger logger = LoggerFactory.getLogger(RoomDaoImpl.class);
//
//    @Autowired
//    CustomShadedJedis roomRedis;
//
//    @Autowired
//    CustomShadedJedis roomIndexRedis;
//
//    @Override
//    public RoomEntity fetchById(int id) {
//        byte[] roomKey = String.valueOf(id).getBytes();
//        Object roomObject = roomRedis.get(roomKey);
//        if (roomObject!=null) {
//            return (RoomEntity) roomObject;
//        }
//        return null;
//    }
//
//    @Override
//    public RoomEntity constructOne() {
//        RoomEntity room = new RoomEntity();
//        // 自增长的 id
//        synchronized ("constructOneRoom") {
//            String lastRoomId = roomIndexRedis.get("lastRoomId");
//            if (lastRoomId==null) {
//                lastRoomId = "10086";
//            }
//            int newRoomId = 1 + Integer.valueOf(lastRoomId);
//            roomIndexRedis.set("lastRoomId", String.valueOf(newRoomId));
//            // 设定房间编号
//            room.setId(newRoomId);
//        }
//        return room;
//    }
//
//    @Override
//    public List<RoomEntity> fetchList(int offset, int limit) {
//        return null;
//    }
//
//    @Override
//    public RoomEntity create(RoomEntity room) throws Exception {
//        byte[] roomKey = String.valueOf(room.getId()).getBytes();
//        roomRedis.set(roomKey, room);
//        Object roomObject = roomRedis.get(roomKey);
//        if (roomObject==null) {
//            throw new Exception("can not save room info");
//        }
//        return (RoomEntity) roomObject;
//    }
//
//    @Override
//    public void update(RoomEntity room) {
//        byte[] roomKey = String.valueOf(room.getId()).getBytes();
//        roomRedis.set(roomKey, room);
//    }
//
//    @Override
//    public int getUserIndex(Long userId) {
//        String roomId = roomIndexRedis.get(userId.toString());
//        if (roomId==null) {
//            return 0;
//        }
//        return Integer.valueOf(roomId);
//    }
//
//    @Override
//    public void setUserIndex(Long userId, int roomId) {
//        roomIndexRedis.set(String.valueOf(userId), String.valueOf(roomId));
//    }
//
//    @Override
//    public void delUserIndex(Long userId) {
//        roomIndexRedis.del(String.valueOf(userId));
//    }
//
//    @Override
//    public void delete(int id) {
//        byte[] roomKey = String.valueOf(id).getBytes();
//        // String roomKey = String.valueOf(id);
//        roomRedis.del(roomKey);
//    }
//
//    @Override
//    public RoomAbstractEntity getRoomAbstract(RoomEntity room) {
//        RoomAbstractEntity roomAbstract = new RoomAbstractEntity();
//        roomAbstract.setId(room.getId());
//        roomAbstract.setName(room.getName());
//        roomAbstract.setOwnerName(room.getOwner().getNickname());
//        roomAbstract.setUserNumber(1);
//        roomAbstract.setHot(0);
//        return roomAbstract;
//    }
//}

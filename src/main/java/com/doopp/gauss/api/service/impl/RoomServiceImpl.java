package com.doopp.gauss.api.service.impl;

import com.doopp.gauss.api.dao.impl.RoomDaoImpl;
import com.doopp.gauss.api.entity.RoomEntity;
import com.doopp.gauss.api.entity.UserEntity;
import com.doopp.gauss.api.service.RoomService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service("roomService")
public class RoomServiceImpl implements RoomService {

    private final Logger logger = LoggerFactory.getLogger(RoomServiceImpl.class);

    @Autowired
    private RoomDaoImpl roomDao;

    @Override
    public RoomEntity createRoom(UserEntity user, String roomName) throws Exception {
        this.leaveRoom(user);
        RoomEntity room = roomDao.constructOne();
        room.setName(roomName);
        room.setOwner(user);
        roomDao.setUserIndex(user.getId(), room.getId());
        return roomDao.create(room);
    }

    @Override
    public RoomEntity joinRoom(int roomId, UserEntity user) {
        this.leaveRoom(user);
        RoomEntity room;
        synchronized (String.valueOf(roomId)) {
            room = roomDao.fetchById(roomId);
            room.joinWatch(user);
            roomDao.update(room);
        }
        roomDao.setUserIndex(user.getId(), room.getId());
        return room;
    }

    @Override
    public void leaveRoom(UserEntity user) {
        int roomId = roomDao.getUserIndex(user.getId());
        if (roomId!=0) {
            synchronized (String.valueOf(roomId)) {
                RoomEntity room = roomDao.fetchById(roomId);
                room.userLeave(user);
                // 如果人都走了，就删除房间
                if (room.getOwner()==null && room.getWatchUsers().size()==0 && room.getFrontUsers().size()==0) {
                    roomDao.delete(roomId);
                }
                // 如果还有人，就更新房间
                else {
                    roomDao.update(room);
                }
            }
        }
        roomDao.delUserIndex(user.getId());
    }

    @Override
    public Map<Integer, RoomEntity> roomList(int pageNumber) {
        return null;
    }
}

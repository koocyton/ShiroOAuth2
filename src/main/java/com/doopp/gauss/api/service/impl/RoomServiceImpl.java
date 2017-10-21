package com.doopp.gauss.api.service.impl;

import com.doopp.gauss.api.dao.impl.RoomDaoImpl;
import com.doopp.gauss.api.entity.RoomEntity;
import com.doopp.gauss.api.entity.UserEntity;
import com.doopp.gauss.api.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service("roomService")
public class RoomServiceImpl implements RoomService {

    @Autowired
    private RoomDaoImpl roomDao;

    @Override
    public RoomEntity createRoom(UserEntity user, String roomName) throws Exception {
        this.leaveRoom(user);
        RoomEntity room = roomDao.constructOne();
        room.setName(roomName);
        room.setOwner(user);
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
        return room;
    }

    @Override
    public void leaveRoom(UserEntity user) {

    }

    @Override
    public Map<Integer, RoomEntity> roomList(int pageNumber) {
        return null;
    }
}

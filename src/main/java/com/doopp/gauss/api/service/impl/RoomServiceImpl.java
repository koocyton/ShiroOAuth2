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
    RoomDaoImpl roomDao;

    @Override
    public RoomEntity createRoom(UserEntity user, String roomName) {
        this.leaveRoom(user);
        RoomEntity room = new RoomEntity();
        room.setId(1000);
        room.setName(roomName);
        room.setOwner(user);
        roomDao.create(room);
        return room;
    }

    @Override
    public RoomEntity joinRoom(int roomId, UserEntity user) {
        return null;
    }

    @Override
    public void leaveRoom(UserEntity user) {

    }

    @Override
    public Map<Integer, RoomEntity> roomList(int pageNumber) {
        return null;
    }
}

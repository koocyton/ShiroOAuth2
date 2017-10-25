package com.doopp.gauss.api.service.impl;

import com.doopp.gauss.api.Exception.EmptyException;
import com.doopp.gauss.api.dao.RoomDao;
import com.doopp.gauss.api.dao.impl.RoomDaoImpl;
import com.doopp.gauss.api.entity.RoomAbstractEntity;
import com.doopp.gauss.api.entity.RoomEntity;
import com.doopp.gauss.api.entity.UserEntity;
import com.doopp.gauss.api.service.RoomService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service("roomService")
public class RoomServiceImpl implements RoomService {

    private final Logger logger = LoggerFactory.getLogger(RoomServiceImpl.class);

    private Map<String, RoomAbstractEntity> roomList = new HashMap<>();

    @Autowired
    private RoomDao roomDao;

    @Override
    public RoomEntity createRoom(UserEntity user, String roomName) throws Exception {
        this.leaveRoom(user);
        RoomEntity room = roomDao.constructOne();
        room.setName(roomName);
        room.setOwner(user);
        roomDao.setUserIndex(user.getId(), room.getId());

        // 应该用面向切面，稍后改
        // 房间摘要信息
        RoomAbstractEntity roomAbstract = roomDao.getRoomAbstract(room);
        roomList.put(String.valueOf(room.getId()), roomAbstract);

        //
        return roomDao.create(room);
    }

    @Override
    public RoomEntity joinRoom(int roomId, UserEntity user) throws EmptyException {
        this.leaveRoom(user);
        RoomEntity room;
        synchronized ("joinLeaveRoom_" + String.valueOf(roomId)) {
            room = roomDao.fetchById(roomId);
            if (room==null) {
                throw new EmptyException("not found room");
            }
            room.joinWatch(user);
            roomDao.update(room);
        }
        roomDao.setUserIndex(user.getId(), room.getId());

        // 应该用面向切面，稍后改
        // change user number
        RoomAbstractEntity roomAbstract = roomList.get(String.valueOf(roomId));
        int userNumber = roomAbstract.getUserNumber();
        roomAbstract.setUserNumber(userNumber+1);
        roomList.put(String.valueOf(roomId), roomAbstract);

        //
        return room;
    }

    @Override
    public void leaveRoom(UserEntity user) {
        int roomId = roomDao.getUserIndex(user.getId());
        if (roomId!=0) {
            synchronized ("joinLeaveRoom_" + String.valueOf(roomId)) {
                RoomEntity room = roomDao.fetchById(roomId);
                room.userLeave(user);

                // 如果人都走了，就删除房间
                if (room.getOwner()==null && room.getWatchUsers().size()==0 && room.getFrontUsers().size()==0) {
                    roomDao.delete(roomId);

                    // 房间空了，就删除房间的摘要
                    roomList.remove(String.valueOf(roomId));
                }
                // 如果还有人，就更新房间
                else {
                    roomDao.update(room);

                    // 应该用面向切面，稍后改
                    // change user number
                    RoomAbstractEntity roomAbstract = roomList.get(String.valueOf(roomId));
                    int userNumber = roomAbstract.getUserNumber();
                    roomAbstract.setUserNumber(userNumber-1);
                    roomList.put(String.valueOf(roomId), roomAbstract);
                }
            }
        }
        roomDao.delUserIndex(user.getId());
    }

    @Override
    public RoomEntity userCurrentRoom(UserEntity user) {
        int roomId = roomDao.getUserIndex(user.getId());
        return (roomId==0) ? null : roomDao.fetchById(roomId);
    }

    @Override
    public Map<String, RoomAbstractEntity> roomList(String rule, int pageNumber) {
        return roomList;
    }
}

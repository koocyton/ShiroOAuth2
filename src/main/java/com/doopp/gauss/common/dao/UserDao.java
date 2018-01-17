package com.doopp.gauss.common.dao;

import com.doopp.gauss.common.entity.Room;
import com.doopp.gauss.common.entity.User;
import org.springframework.stereotype.Repository;
import org.springframework.web.socket.WebSocketSession;

import javax.annotation.Resource;

/*
 * Created by henry on 2017/7/4.
 */
@Repository("userDao")
public class UserDao {

    @Resource
    private RoomDao roomDao;

    // 拿到用户
    public User getUserBySocketSession(WebSocketSession socketSession) {
        return (User) socketSession.getAttributes().get("sessionUser");
    }

    // 拿到房间里的用户
    public User[] getUsersInRoom(WebSocketSession socketSession) {
        int roomId = (int) socketSession.getAttributes().get("sessionRoomId");
        return this.getUsersInRoom(roomId);
    }

    // 拿到房间里的用户
    public User[] getUsersInRoom(int roomId) {
        Room room = roomDao.getRoomById(roomId);
        return room.getUsers();
    }
}

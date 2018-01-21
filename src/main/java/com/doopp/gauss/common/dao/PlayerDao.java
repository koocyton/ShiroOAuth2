package com.doopp.gauss.common.dao;

import com.doopp.gauss.common.entity.Room;
import com.doopp.gauss.common.entity.Player;
import org.springframework.stereotype.Repository;
import org.springframework.web.socket.WebSocketSession;

import javax.annotation.Resource;

@Repository("playerDao")
public class PlayerDao {

    @Resource
    private RoomDao roomDao;

    // 拿到房间里的用户
    public Player[] getPlayersInRoom(WebSocketSession socketSession) {
        int roomId = (int) socketSession.getAttributes().get("sessionRoomId");
        return this.getPlayersInRoom(roomId);
    }

    // 拿到房间里的用户
    private Player[] getPlayersInRoom(int roomId) {
        Room room = roomDao.getRoomById(roomId);
        return room.getPlayers();
    }
}

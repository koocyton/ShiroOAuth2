package com.doopp.gauss.common.dao;

import com.doopp.gauss.common.entity.Room;
import com.doopp.gauss.common.entity.Player;
import com.doopp.gauss.server.websocket.GameSocketHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.socket.WebSocketSession;

import javax.annotation.Resource;

@Repository("playerDao")
public class PlayerDao {

    // logger
    private final static Logger logger = LoggerFactory.getLogger(PlayerDao.class);

    @Resource
    private RoomDao roomDao;

    // socket handle
    @Autowired
    private GameSocketHandler gameSocketHandler;

    // 获取房间内的所有用户
    public Player[] getPlayersByRoom(Room room) {
        return room.getPlayers();
    }

    // 获取 player
    public Player getPlayerById(Long playerId) {
        WebSocketSession socketSession = gameSocketHandler.getWebsocketById(playerId);
        return (Player) socketSession.getAttributes().get("sessionPlayer");
    }

    // 用户离开房间
    public void playerLeaveRoom(Room room, Player player) {
        Player[] players = room.getPlayers();
        for(int ii=0; ii<players.length; ii++) {
            if (players[ii].getId().equals(player.getId())) {
                players[ii].setRoomId(0);
                players[ii] = null;
                break;
            }
        }
    }

    // 用户加入房间
    public void playerJoinRoom(Player player) {
        Room room = roomDao.getFreeRoom();
        if (room==null) {
            room = roomDao.createRoom();
        }
        Player[] players = room.getPlayers();
        for(int ii=0; ii<players.length; ii++) {
            if (players[ii]==null) {
                players[ii] = player;
                players[ii].setRoomId(room.getId());
            }
        }
    }
}

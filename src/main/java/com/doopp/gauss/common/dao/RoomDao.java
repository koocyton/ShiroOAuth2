package com.doopp.gauss.common.dao;

import com.doopp.gauss.common.entity.Room;
import com.doopp.gauss.common.entity.Player;
import com.doopp.gauss.server.websocket.GameSocketHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository("roomDao")
public class RoomDao {

    // logger
    private final static Logger logger = LoggerFactory.getLogger(RoomDao.class);

    // socket handle
    @Autowired
    private GameSocketHandler gameSocketHandler;

    // 拿到房间
    public Room getRoomById(int roomId) {
        return gameSocketHandler.getRoomById(roomId);
    }

    // 所有房间
    public Map<Integer, Integer> getFreeRoomIds() {
        return gameSocketHandler.getFreeRoomIds();
    }

    // 用户离开房间
    public void playerLeaveRoom(int roomId, Player player) {
        Room room = this.getRoomById(roomId);
        this.playerLeave(room, player);
    }

    // 用户加入房间
    public Room playerJoinRoom(Player player) {
        if (this.getFreeRoomIds().size()>=1) {
            for (Integer roomId : this.getFreeRoomIds().values()) {
                return this.getRoomById(roomId);
            }
        }
        Room room = playerJoinSpaceRoom(player);
        player.setRoomId(room.getId());
        return room;
    }

    // 用户加入指定房间
    private Room playerJoinByRoom(Room room, Player player) {
        room.playerJoin(player);
        return room;
    }

    // 用户加入空房间
    private Room playerJoinSpaceRoom(Player player) {
        Room room = new Room();
        room.setId(++lastRoomId);
        room.setPlayers(new Player[]{player});
        gameSocketHandler.addRoom(roomId);
        return room;
    }

    public void playerLeave(Room room, int index) {
        Player[] players = room.getPlayers();
        players[index] = null;
    }

    public void playerLeave(Room room, Player player) {
        Player[] players = room.getPlayers();
        for(int ii=0; ii<players.length; ii++) {
            if (players[ii].getId().equals(player.getId())) {
                players[ii] = null;
                break;
            }
        }
    }

    public void playerJoin(Room room, Player player) {
        Player[] players = room.getPlayers();
        for(int ii=0; ii<players.length; ii++) {
            if (players[ii].getId().equals(player.getId())) {
                players[ii] = null;
                break;
            }
        }
    }
}

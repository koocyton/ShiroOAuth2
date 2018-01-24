package com.doopp.gauss.common.dao;

import com.doopp.gauss.common.entity.PlayerAction;
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

    // 获取房间里的狼
    public Player[] getWolfByRoom(Room room) {
        int[] seats = room.getWolfSeat();
        Player[] wolfs = new Player[]{};
        Player[] players = room.getPlayers();
        for(int ii=0; ii<seats.length; ii++) {
            int nn = seats[ii];
            wolfs[ii] = players[nn];
        }
        return wolfs;
    }

    // 获取房间里的村民
    public Player[] getVillagerByRoom(Room room) {
        int[] seats = room.getVillagerSeat();
        Player[] villagers = new Player[]{};
        Player[] players = room.getPlayers();
        for(int ii=0; ii<seats.length; ii++) {
            int nn = seats[ii];
            villagers[ii] = players[nn];
        }
        return villagers;
    }

    // 获取房间里的先知
    public Player getSeerByRoom(Room room) {
        int nn = room.getSeerSeat();
        Player[] players = room.getPlayers();
        return players[nn];
    }

    // 获取房间里的猎人
    public Player getHunterByRoom(Room room) {
        int nn = room.getSeerSeat();
        Player[] players = room.getPlayers();
        return players[nn];
    }

    // 获取房间里的女巫
    public Player getWitchByRoom(Room room) {
        int nn = room.getWitchSeat();
        Player[] players = room.getPlayers();
        return players[nn];
    }

    // 获取房间里的丘比特
    public Player getCupidByRoom(Room room) {
        int nn = room.getCupidSeat();
        Player[] players = room.getPlayers();
        return players[nn];
    }

    // 记录用户的操作
    public void cacheAction(String action, Player actionPlayer, Player targetPlayer) {
        if (actionPlayer.getRoomId()==targetPlayer.getRoomId()) {
            Room room = roomDao.getRoomById(actionPlayer.getRoomId());
            room.addCacheAction(new PlayerAction(action, actionPlayer, targetPlayer));
        }
    }

    // 用户离开房间
    public void playerLeaveRoom(Player player) {
        Room room = roomDao.getRoomById(player.getRoomId());
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
                return;
            }
        }
    }
}

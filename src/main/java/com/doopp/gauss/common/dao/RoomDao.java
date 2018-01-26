package com.doopp.gauss.common.dao;

import com.doopp.gauss.common.entity.Player;
import com.doopp.gauss.common.entity.PlayerAction;
import com.doopp.gauss.common.entity.Room;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Repository("roomDao")
public class RoomDao {

    // logger
    private final static Logger logger = LoggerFactory.getLogger(RoomDao.class);

    // room`s session
    private static final Map<Integer, Room> rooms = new HashMap<>();

    // freeRoom`s session
    private static final Map<Integer, Integer> freeRoomIds = new HashMap<>();

    // last room id
    private int lastRoomId = 51263;

    // get a free room
    public Room getFreeRoom() {
        Iterator<Integer> iterator = freeRoomIds.values().iterator();
        if (iterator.hasNext()) {
            return this.getRoomById(iterator.next());
        }
        return  null;
    }

    // get room by id
    public Room getRoomById(int roomId) {
        return rooms.get(roomId);
    }

    // create a free room
    public Room createRoom() {
        Room room = new Room();
        room.setId(++lastRoomId);
        rooms.put(room.getId(), room);
        freeRoomIds.put(room.getId(), room.getId());
        return room;
    }

    public void removeRoom(Room room) {
        rooms.remove(room.getId());
    }

    // 记录用户的操作
    public void cacheAction(String action, Player actionPlayer, Player targetPlayer) {
        if (actionPlayer.getRoomId()==targetPlayer.getRoomId()) {
            Room room = this.getRoomById(actionPlayer.getRoomId());
            room.setCacheAction(new PlayerAction(action, actionPlayer, targetPlayer));
        }
    }

    // 投票结果
    public Player mostTargetPlayer(Room room, String action) {
        Map<Long, PlayerAction> playerActions = room.getCacheActions(action);
        Map<Long, Integer> actionNumbers = new HashMap<>();
        for(PlayerAction playerAction : playerActions.values()) {
            Long actionKey = playerAction.getTargetPlayer().getId();
            Integer actionNumber = actionNumbers.get(actionKey);
            actionNumber = (actionNumber==null) ? 1 : actionNumber + 1;
            actionNumbers.put(actionKey, actionNumber);
            if (actionNumber>playerActions.size()/2) {
                return playerAction.getTargetPlayer();
            }
        }
        return null;
    }

    // 用户离开房间
    public void playerLeaveRoom(Player player) {
        Room room = this.getRoomById(player.getRoomId());
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
        Room room = this.getFreeRoom();
        if (room==null) {
            room = this.createRoom();
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

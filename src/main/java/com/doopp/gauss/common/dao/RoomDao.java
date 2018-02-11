//package com.doopp.gauss.common.dao;
//
//import com.doopp.gauss.common.defined.Action;
//import com.doopp.gauss.common.entity.Player;
//import com.doopp.gauss.common.entity.PlayerAction;
//import com.doopp.gauss.common.entity.Room;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Repository;
//
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.Map;
//
//@Repository("roomDao")
//public class RoomDao {
//
//    // logger
//    private final static Logger logger = LoggerFactory.getLogger(RoomDao.class);
//
//    // room`s session
//    private static final Map<Integer, Room> roomGroup = new HashMap<>();
//
//    // freeRoom`s session
//    private static final Map<Integer, Integer> freeRoomIdGroup = new HashMap<>();
//
//    // last room id
//    private int lastRoomId = 51263;
//
//    // get a free room
//    public Room getFreeRoom() {
//        Iterator<Integer> iterator = freeRoomIdGroup.values().iterator();
//        if (iterator.hasNext()) {
//            return this.getRoomById(iterator.next());
//        }
//        return  null;
//    }
//
//    // get room by id
//    public Room getRoomById(int roomId) {
//        return roomGroup.get(roomId);
//    }
//
//    // create a free room
//    public Room createRoom() {
//        Room room = new Room();
//        room.setId(++lastRoomId);
//        room.setWaitAction(Action.PLAYER_READY);
//        roomGroup.put(room.getId(), room);
//        freeRoomIdGroup.put(room.getId(), room.getId());
//        return room;
//    }
//
//    public void removeRoom(Room room) {
//        roomGroup.remove(room.getId());
//    }
//
//    // 记录用户的操作
//    public void cacheAction(String action, Player actionPlayer, Player targetPlayer) {
//        if (actionPlayer.getRoom_id()==targetPlayer.getRoom_id()) {
//            Room room = this.getRoomById(actionPlayer.getRoom_id());
//            room.setCacheAction(new PlayerAction(action, actionPlayer, targetPlayer));
//        }
//    }
//
//    // 投票结果
//    public Player mostTargetPlayer(Room room, String action) {
//        Map<Long, PlayerAction> playerActions = room.getCacheActions(action);
//        Map<Long, Integer> actionNumbers = new HashMap<>();
//        for(PlayerAction playerAction : playerActions.values()) {
//            Long actionKey = playerAction.getTargetPlayer().getId();
//            Integer actionNumber = actionNumbers.get(actionKey);
//            actionNumber = (actionNumber==null) ? 1 : actionNumber + 1;
//            actionNumbers.put(actionKey, actionNumber);
//            if (actionNumber>playerActions.size()/2) {
//                return playerAction.getTargetPlayer();
//            }
//        }
//        return null;
//    }
//
//    // 用户离开房间
//    public void playerLeaveRoom(Player player) {
//        Room room = this.getRoomById(player.getRoom_id());
//        if (room!=null) {
//            Player[] players = room.getPlayers();
//            for (int ii = 0; ii < players.length; ii++) {
//                if (players[ii]!=null && players[ii].getId().equals(player.getId())) {
//                    players[ii].setRoom_id(0);
//                    players[ii] = null;
//                    break;
//                }
//            }
//        }
//    }
//
//    // 用户加入房间
//    public void playerJoinRoom(Player player) {
//        Room room = this.getFreeRoom();
//        if (room==null) {
//            room = this.createRoom();
//        }
//        Player[] players = room.getPlayers();
//        for(int ii=0; ii<players.length; ii++) {
//            if (players[ii]==null) {
//                players[ii] = player;
//                players[ii].setRoom_id(room.getId());
//                players[ii].setSeat(ii);
//                return;
//            }
//        }
//        logger.info(" >>> room " + room);
//    }
//}

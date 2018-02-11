//package com.doopp.gauss.common.dao;
//
//import com.doopp.gauss.common.entity.Room;
//import com.doopp.gauss.common.entity.Player;
//import com.doopp.gauss.common.service.GameService;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Repository;
//import org.springframework.web.socket.WebSocketSession;
//
//
//@Repository("playerDao")
//public class PlayerDao {
//
//    // logger
//    private final static Logger logger = LoggerFactory.getLogger(PlayerDao.class);
//
//
//    // socket handle
//    @Autowired
//    private GameService gameService;
//
//    // 获取房间内的所有用户
//    public Player[] getPlayersByRoom(Room room) {
//        return room.getPlayers();
//    }
//
//    // 获取 player
//    public Player getPlayerById(Long playerId) {
//        return null;
//    }
//
//    // 获取房间里的狼
//    public Player[] getWolfsByRoom(Room room) {
//        int[] seats = room.getWolfSeat();
//        Player[] wolfs = new Player[]{};
//        Player[] players = room.getPlayers();
//        for(int ii=0; ii<seats.length; ii++) {
//            int nn = seats[ii];
//            wolfs[ii] = players[nn];
//        }
//        return wolfs;
//    }
//
//    // 获取房间里的村民
//    public Player[] getVillagersByRoom(Room room) {
//        int[] seats = room.getVillagerSeat();
//        Player[] villagers = new Player[]{};
//        Player[] players = room.getPlayers();
//        for(int ii=0; ii<seats.length; ii++) {
//            int nn = seats[ii];
//            villagers[ii] = players[nn];
//        }
//        return villagers;
//    }
//
//    // 获取房间里的先知
//    public Player getSeerByRoom(Room room) {
//        int nn = room.getSeerSeat();
//        Player[] players = room.getPlayers();
//        return players[nn];
//    }
//
//    // 获取房间里的猎人
//    public Player getHunterByRoom(Room room) {
//        int nn = room.getSeerSeat();
//        Player[] players = room.getPlayers();
//        return players[nn];
//    }
//
//    // 获取房间里的女巫
//    public Player getWitchByRoom(Room room) {
//        int nn = room.getWitchSeat();
//        Player[] players = room.getPlayers();
//        return players[nn];
//    }
//
//    // 获取房间里的丘比特
//    public Player getCupidByRoom(Room room) {
//        int nn = room.getCupidSeat();
//        Player[] players = room.getPlayers();
//        return players[nn];
//    }
//}

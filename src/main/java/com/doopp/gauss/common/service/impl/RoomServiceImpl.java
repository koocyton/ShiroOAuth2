package com.doopp.gauss.common.service.impl;

import com.doopp.gauss.common.defined.Action;
import com.doopp.gauss.common.entity.Player;
import com.doopp.gauss.common.entity.Room;
import com.doopp.gauss.common.service.GameService;
import com.doopp.gauss.common.service.RoomService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service("roomService")
public class RoomServiceImpl implements RoomService {

    // logger
    private final static Logger logger = LoggerFactory.getLogger(RoomServiceImpl.class);

    private final static Map<Integer, Room> roomGroup = new HashMap<>();

    private final static Map<Integer, Integer> spaceRoomGroup = new HashMap<>();

    private static int roomId = 51263;

    @Autowired
    private GameService gameService;

    // 用户进入房间
    @Override
    public void playerJoin(Player player) {
        this.joinRoom(player);
    }

    // 用户离开房间
    @Override
    public void playerLeave(Player player) {
        this.leaveRoom(player);
    }

    // get a free room
    private void joinRoom(Player player) {
        Room room;
        synchronized (RoomService.class) {
            if (spaceRoomGroup.size()==0) {
                room = this.createSpaceRoom(player);
            }
            else {
                int roomId = (Integer)spaceRoomGroup.values().toArray()[0];
                room = this.getRoom(roomId);
                room.addPlayer(player);
                if (room.noSeat()) {
                    spaceRoomGroup.remove(roomId);
                }
            }
        }
        if (room!=null) {
            gameService.sendMessage(room, Action.PLAYER_JOIN, room.getSeats());
        }
    }

    private Room createSpaceRoom(Player player)
    {
        synchronized (RoomService.class) {
            Room room = new Room();
            room.setId(++roomId);
            room.setAcceptAction(Action.PLAYER_READY);
            room.addPlayer(player);
            roomGroup.put(room.getId(), room);
            spaceRoomGroup.put(room.getId(), room.getId());
            return room;
        }
    }

    // get a free room
    private void leaveRoom(Player player) {
        Room room;
        synchronized (RoomService.class) {
            int roomId = player.getRoom_id();
            room = this.getRoom(roomId);
            if (room!=null) {
                room.delPlayer(player);
                spaceRoomGroup.put(roomId, roomId);
            }
        }
        if (room!=null) {
            gameService.sendMessage(room, Action.PLAYER_LEAVE, room.getSeats());
        }
    }

    // get room by id
    @Override
    public Room getRoom(int roomId) {
        return roomGroup.get(roomId);
    }
}

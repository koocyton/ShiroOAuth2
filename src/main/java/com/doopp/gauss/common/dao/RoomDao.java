package com.doopp.gauss.common.dao;

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
}

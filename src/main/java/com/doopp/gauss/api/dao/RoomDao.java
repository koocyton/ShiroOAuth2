package com.doopp.gauss.api.dao;

import com.doopp.gauss.api.entity.RoomEntity;

public interface RoomDao {

    RoomEntity fetchById(int id);

    void create(RoomEntity room);

    void update(RoomEntity room);

    void delete(int id);
}

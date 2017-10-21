package com.doopp.gauss.api.dao;

import com.doopp.gauss.api.entity.RoomEntity;

import java.util.List;

public interface RoomDao {

    RoomEntity constructOne();

    List<RoomEntity> fetchList(int offset, int limit);

    RoomEntity create(RoomEntity room) throws Exception;

    RoomEntity fetchById(int id);

    void update(RoomEntity room);

    void delete(int id);

    int getUserIndex(Long userId);

    void setUserIndex(Long userId, int roomId);

    void delUserIndex(Long userId);
}

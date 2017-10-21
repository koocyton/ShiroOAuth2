package com.doopp.gauss.api.service;

import com.doopp.gauss.api.entity.RoomEntity;
import com.doopp.gauss.api.entity.UserEntity;
import java.util.Map;

public interface RoomService {


    /**
     * 创建房间
     *
     * @param user 创建人
     * @param roomName 房间名
     * @return 房间信息
     */
    RoomEntity createRoom(UserEntity user, String roomName) throws Exception;


    /**
     * 加入房间
     *
     * @param roomId 房间 ID
     * @param user 加入的人
     * @return 房间信息
     */
    RoomEntity joinRoom(int roomId, UserEntity user);


    /**
     * 离开房间
     *
     * @param user 用户
     */
    void leaveRoom(UserEntity user);

    /**
     * 列出当前房间列表
     *
     * @param pageNumber 第几页
     * @return 列表
     */
    Map<Integer, RoomEntity> roomList(int pageNumber);
}
